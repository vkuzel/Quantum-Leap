package cz.quantumleap.core.data.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.quantumleap.core.data.EnumManager;
import cz.quantumleap.core.data.LookupDao;
import cz.quantumleap.core.data.LookupDaoManager;
import cz.quantumleap.core.data.primarykey.PrimaryKeyResolver;
import cz.quantumleap.core.data.transport.*;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.jooq.Table;
import org.jooq.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;

public class MapperFactory {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final org.jooq.Table<? extends Record> table;
    private final PrimaryKeyResolver primaryKeyResolver;
    private final LookupDaoManager lookupDaoManager;
    private final EnumManager enumManager;

    public MapperFactory(Table<? extends Record> table, PrimaryKeyResolver primaryKeyResolver, LookupDaoManager lookupDaoManager, EnumManager enumManager) {
        this.table = table;
        this.primaryKeyResolver = primaryKeyResolver;
        this.lookupDaoManager = lookupDaoManager;
        this.enumManager = enumManager;
    }

    public SliceMapper createSliceMapper(SliceRequest sliceRequest, List<TablePreferences> tablePreferencesList) {
        return new SliceMapper(table, primaryKeyResolver, lookupDaoManager, enumManager, sliceRequest, tablePreferencesList);
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
            if (hasComplexValueGetters()) {
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
                Object value = getValue(transport, getter.getKey());
                if (value instanceof Lookup) {
                    value = ((Lookup) value).getId();
                } else if (value instanceof EnumValue) {
                    value = ((EnumValue) value).getId();
                } else if (value instanceof Map) {
                    value = OBJECT_MAPPER.convertValue(value, JsonNode.class);
                } else {
                    value = getValue(transport, getter.getKey());
                }

                DataType<?> dataType = field.getDataType();
                if (value != null || dataType.nullable()) {
                    record.setValue(castField(field), value);
                }
            }
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

        private boolean hasComplexValueGetters() {
            for (Pair<Method, Class<?>> getter : transportGetters.values()) {
                Class<?> type = getter.getValue();
                if (type == Lookup.class || type == EnumValue.class || type == Set.class) {
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
            if (hasComplexValueSetters()) { // TODO Do not evaluate this on each map call!
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

                        String databaseTableNameWithSchema = convertFieldToDatabaseTableNameWithSchema(field);
                        LookupDao lookupDao = lookupDaoManager.getDaoByDatabaseTableNameWithSchema(databaseTableNameWithSchema);
                        Validate.notNull(lookupDao, "LookupDao for field " + transport.getClass().getSimpleName() + "." + fieldName + " was not found!");

                        String label = lookupDao.fetchLabelById(referenceId);
                        value = new Lookup(referenceId, label, databaseTableNameWithSchema);
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
                } else if (paramType == Map.class) {
                    JsonNode jsonNode = record.getValue(field, JsonNode.class);
                    if (!jsonNode.isNull()) {
                        value = OBJECT_MAPPER.convertValue(jsonNode, Map.class);
                    }
                } else {
                    value = record.getValue(field, paramType);
                }

                if (value != null) {
                    setValue(transport, setter.getKey(), value);
                }
            }
        }

        private String convertFieldToDatabaseTableNameWithSchema(Field<?> field) {
            for (ForeignKey<? extends Record, ?> foreignKey : table.getReferences()) {
                if (foreignKey.getFields().size() != 1) {
                    continue;
                }

                TableField<? extends Record, ?> tableField = foreignKey.getFields().get(0);
                if (!tableField.equals(field)) {
                    continue;
                }

                return MapperUtils.resolveDatabaseTableNameWithSchema(foreignKey.getKey().getTable());
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
                return transportType.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }

        private boolean hasComplexValueSetters() {
            for (Pair<Method, Class<?>> setter : transportSetters.values()) {
                Class<?> type = setter.getValue();
                if (type == Lookup.class || type == EnumValue.class || type == Set.class || type == Map.class) {
                    return true;
                }
            }
            return false;
        }

        private Pair<Method, Class<?>> getSetter(Field<?> field) {
            String fieldName = field.getName();
            String setterName = "set" + LOWER_UNDERSCORE.to(UPPER_CAMEL, fieldName.toLowerCase());
            return transportSetters.get(setterName);
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
