package cz.quantumleap.core.data.mapper;

import cz.quantumleap.core.data.LookupDao;
import cz.quantumleap.core.data.LookupDaoManager;
import cz.quantumleap.core.data.transport.Lookup;
import cz.quantumleap.core.data.transport.SliceRequest;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.jooq.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;

public class MapperFactory {

    private final org.jooq.Table<? extends Record> table;
    private final LookupDaoManager lookupDaoManager;

    public MapperFactory(org.jooq.Table<? extends Record> table, LookupDaoManager lookupDaoManager) {
        this.table = table;
        this.lookupDaoManager = lookupDaoManager;
    }

    public SliceMapper createSliceMapper(SliceRequest sliceRequest) {
        return new SliceMapper(table, lookupDaoManager, sliceRequest);
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
            if (hasLookupGetters()) {
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
                } else {
                    value = getValue(transport, getter.getKey());
                }

                if (value != null) {
                    record.setValue((Field<Object>) field, value);
                }
            }
        }

        private Object getValue(T transport, Method getter) {
            try {
                return getter.invoke(transport);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException(e);
            }
        }

        private boolean hasLookupGetters() {
            for (Pair<Method, Class<?>> getter : transportGetters.values()) {
                if (getter.getValue() == Lookup.class) {
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
                // A setter with a single parameter only
                if (!method.getName().startsWith("get") || method.getParameterCount() != 0) {
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
            if (hasLookupSetters()) {
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

                        String databaseTableNameWithSchema = getDatabaseTableNameWithSchemaForLookupField(field);
                        LookupDao lookupDao = lookupDaoManager.getDaoByDatabaseTableNameWithSchema(databaseTableNameWithSchema);
                        Validate.notNull(lookupDao, "LookupDao for field " + transport.getClass().getSimpleName() + "." + fieldName + " was not found!");

                        String label = lookupDao.fetchLabelById(referenceId);
                        value = new Lookup(referenceId, label, databaseTableNameWithSchema);
                    }
                } else {
                    value = record.getValue(field, paramType);
                }

                if (value != null) {
                    setValue(transport, setter.getKey(), value);
                }
            }
        }

        private String getDatabaseTableNameWithSchemaForLookupField(Field<?> field) {
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

        private boolean hasLookupSetters() {
            for (Pair<Method, Class<?>> setter : transportSetters.values()) {
                if (setter.getValue() == Lookup.class) {
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
