package cz.quantumleap.core.data.mapper;

import cz.quantumleap.core.data.EnumManager;
import cz.quantumleap.core.data.LookupDao;
import cz.quantumleap.core.data.LookupDaoManager;
import cz.quantumleap.core.data.entity.Entity;
import cz.quantumleap.core.data.entity.EntityIdentifier;
import cz.quantumleap.core.data.transport.*;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.jooq.Table;
import org.jooq.*;
import org.jooq.types.YearToSecond;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;

public class MapperFactory<TABLE extends Table<? extends Record>> {

    private final Entity<TABLE> entity;
    private final ConverterProvider converterProvider;
    private final LookupDaoManager lookupDaoManager;
    private final EnumManager enumManager;

    public MapperFactory(Entity<TABLE> entity, DSLContext dslContext, LookupDaoManager lookupDaoManager, EnumManager enumManager) {
        this.entity = entity;
        this.converterProvider = dslContext.configuration().converterProvider();
        this.lookupDaoManager = lookupDaoManager;
        this.enumManager = enumManager;
    }

    public SliceMapper<TABLE> createSliceMapper(SliceRequest sliceRequest, List<TablePreferences> tablePreferencesList) {
        return new SliceMapper<>(entity, lookupDaoManager, enumManager, sliceRequest, tablePreferencesList);
    }

    public <T> TransportUnMapper<T> createTransportUnMapper(Class<T> transportType) {
        return new TransportUnMapper<>(transportType);
    }

    public <T> TransportMapper<T> createTransportMapper(Class<T> transportType) {
        return new TransportMapper<>(transportType);
    }

    public class TransportUnMapper<T> {

        private final Map<String, Pair<Method, Class<?>>> transportGetters;

        private TransportUnMapper(Class<T> transportType) {
            this.transportGetters = getInstanceGetters(transportType);
        }

        public Record unMap(T transport, Record record) {
            if (hasCustomConvertibleTypes(record.fields())) {
                for (Field<?> field : record.fields()) {
                    setValueToRecordField(transport, field, record);
                }
            } else {
                record.from(transport);
            }
            return record;
        }

        private void setValueToRecordField(T transport, Field<?> field, Record record) {
            Pair<Method, Class<?>> getter = getGetter(field);
            if (getter != null) {
                DataType<?> databaseType = field.getDataType();
                Object value = getValue(transport, getter.getKey());
                if (value instanceof Lookup) {
                    value = ((Lookup<?>) value).getId();
                } else if (value instanceof EnumValue) {
                    value = ((EnumValue) value).getId();
                }

                if (value != null) {
                    Class<?> userType = value.getClass();
                    Converter<Object, Object> converter = resolveConverter(databaseType, userType);
                    record.setValue(castField(field), value, converter);
                } else if (databaseType.nullable()) {
                    record.setValue(castField(field), null);
                }
            }
        }

        @SuppressWarnings("unchecked")
        private Converter<Object, Object> resolveConverter(DataType<?> dataType, Class<?> userType) {
            Class<?> databaseType = dataType.getType();
            return (Converter<Object, Object>) converterProvider.provide(databaseType, userType);
        }

        @SuppressWarnings("unchecked")
        private Field<Object> castField(Field<?> field) {
            return (Field<Object>) field;
        }

        private Object getValue(T transport, Method getter) {
            try {
                return getter.invoke(transport);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException(e);
            }
        }

        private boolean hasCustomConvertibleTypes(Field<?>[] fields) {
            for (Pair<Method, Class<?>> getter : transportGetters.values()) {
                Class<?> type = getter.getValue();
                if (type == Lookup.class || type == EnumValue.class || type == Set.class) {
                    return true;
                }
            }
            // jOOQ currently does not use converters for unmapping, provided
            // by a custom ConverterProvider. In that case, the converter has
            // to be invoked manually.
            for (Field<?> field : fields) {
                Class<?> type = field.getDataType().getType();
                if (type == JSON.class || type == YearToSecond.class) {
                    return true;
                }
            }
            return false;
        }

        private Pair<Method, Class<?>> getGetter(Field<?> field) {
            String fieldName = field.getName();
            String withoutPrefix = LOWER_UNDERSCORE.to(UPPER_CAMEL, fieldName.toLowerCase());

            String setGetterName = "get" + withoutPrefix;
            Pair<Method, Class<?>> getter = transportGetters.get(setGetterName);
            if (getter != null) {
                return getter;
            }

            String isGetterName = "is" + withoutPrefix;
            return transportGetters.get(isGetterName);
        }

        private Map<String, Pair<Method, Class<?>>> getInstanceGetters(Class<?> type) {
            Method[] methods = type.getMethods();
            Map<String, Pair<Method, Class<?>>> getters = new HashMap<>(methods.length / 2);
            for (Method method : methods) {
                // Non static methods only
                if ((method.getModifiers() & Modifier.STATIC) != 0) {
                    continue;
                }
                // A getter with no parameters only
                if (!(method.getName().startsWith("get") || method.getName().startsWith("is")) || method.getParameterCount() != 0) {
                    continue;
                }

                getters.put(
                        method.getName(),
                        Pair.of(method, method.getReturnType())
                );
            }
            return getters;
        }
    }

    public class TransportMapper<T> implements RecordMapper<Record, T> {

        private final Class<T> transportType;
        private final Map<String, Pair<Method, Class<?>>> transportSetters;

        private TransportMapper(Class<T> transportType) {
            this.transportType = transportType;
            this.transportSetters = getInstanceSetters(transportType);
        }

        @Override
        public T map(Record record) {
            if (hasCustomConvertibleTypes()) {
                T transport = createTransportObject();
                for (Field<?> field : record.fields()) {
                    setValueToTransportMember(transport, record, field);
                }
                return transport;
            } else {
                return record.into(transportType);
            }
        }

        private void setValueToTransportMember(T transport, Record record, Field<?> field) {
            Pair<Method, Class<?>> setter = getSetter(field);
            if (setter != null) {
                Class<?> paramType = setter.getValue();

                Object value = null;
                if (paramType == Lookup.class) {
                    Object referenceId = record.getValue(field);
                    if (referenceId != null) {
                        String fieldName = field.getName();
                        EntityIdentifier<?> entityIdentifier = convertFieldToLookupIdentifier(field);
                        LookupDao<?> lookupDao = lookupDaoManager.getDaoByEntityIdentifier(entityIdentifier);
                        String msg = String.format("LookupDao for field %s.%s identified by %s was not found!",
                                transport.getClass().getSimpleName(), fieldName, entityIdentifier);
                        Validate.notNull(lookupDao, msg);

                        String label = lookupDao.fetchLabelById(referenceId);
                        value = new Lookup<>(referenceId, label, entityIdentifier);
                    }
                } else if (paramType == EnumValue.class) {
                    String referenceId = record.getValue(field, String.class);
                    if (referenceId != null) {
                        String enumId = MapperUtils.resolveEnumId(field);
                        value = enumManager.createEnumValue(enumId, referenceId);
                    }
                } else if (paramType == Set.class) {
                    String[] referenceIds = record.getValue(field, String[].class);
                    if (referenceIds != null) {
                        String enumId = MapperUtils.resolveEnumId(field);
                        value = enumManager.createSet(enumId, Arrays.asList(referenceIds));
                    }
                } else {
                    value = record.getValue(field, paramType);
                }

                if (value != null) {
                    setValue(transport, setter.getKey(), value);
                }
            }
        }

        private EntityIdentifier<?> convertFieldToLookupIdentifier(Field<?> field) {
            EntityIdentifier<?> entityIdentifier = entity.getLookupFieldsMap().get(field);
            if (entityIdentifier != null) {
                return entityIdentifier;
            }

            for (ForeignKey<? extends Record, ?> reference : entity.getTable().getReferences()) {
                if (reference.getFields().size() != 1) {
                    continue;
                }

                TableField<? extends Record, ?> tableField = reference.getFields().get(0);
                if (!tableField.equals(field)) {
                    continue;
                }

                return EntityIdentifier.forTable(reference.getKey().getTable());
            }
            return null;
        }

        private void setValue(T transport, Method setter, Object value) {
            try {
                setter.invoke(transport, value);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException(e);
            }
        }

        private T createTransportObject() {
            try {
                Constructor<T> constructor = transportType.getConstructor();
                return constructor.newInstance();
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException(e);
            }
        }

        private boolean hasCustomConvertibleTypes() {
            for (Pair<Method, Class<?>> setter : transportSetters.values()) {
                Class<?> type = setter.getValue();
                if (type == Lookup.class || type == EnumValue.class || type == Set.class) {
                    return true;
                }
            }
            return false;
        }

        private Pair<Method, Class<?>> getSetter(Field<?> field) {
            String fieldName = field.getName();
            String setterName = "set" + LOWER_UNDERSCORE.to(UPPER_CAMEL, fieldName.toLowerCase());
            Pair<Method, Class<?>> getter = transportSetters.get(setterName);
            if (getter != null) {
                return getter;
            }

            if (fieldName.startsWith("is_")) {
                String normalizedFieldName = fieldName.substring(3);
                String normalizedSetterName = "set" + LOWER_UNDERSCORE.to(UPPER_CAMEL, normalizedFieldName.toLowerCase());
                return transportSetters.get(normalizedSetterName);
            }

            return null;
        }

        private Map<String, Pair<Method, Class<?>>> getInstanceSetters(Class<?> type) {
            Method[] methods = type.getMethods();
            Map<String, Pair<Method, Class<?>>> setters = new HashMap<>(methods.length / 2);
            for (Method method : methods) {
                // Non static methods only
                if ((method.getModifiers() & Modifier.STATIC) != 0) {
                    continue;
                }
                // A setter with a single parameter only
                if (!method.getName().startsWith("set") || method.getParameterCount() != 1) {
                    continue;
                }

                setters.put(
                        method.getName(),
                        Pair.of(method, method.getParameterTypes()[0])
                );
            }
            return setters;
        }
    }
}
