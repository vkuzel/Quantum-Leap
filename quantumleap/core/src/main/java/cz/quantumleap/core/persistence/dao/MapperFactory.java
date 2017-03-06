package cz.quantumleap.core.persistence.dao;

import com.google.common.collect.*;
import cz.quantumleap.core.persistence.dao.LookupDao;
import cz.quantumleap.core.persistence.dao.lookup.LookupDaoManager;
import cz.quantumleap.core.persistence.transport.Lookup;
import cz.quantumleap.core.persistence.transport.NamedTable;
import cz.quantumleap.core.persistence.transport.NamedTable.Column;
import cz.quantumleap.core.persistence.transport.Slice;
import cz.quantumleap.core.persistence.transport.SliceRequest;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jooq.*;
import org.jooq.Table;
import org.springframework.data.domain.Sort;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.CaseFormat.*;

public class MapperFactory {

    private final Table<Record> table;
    private final LookupDaoManager lookupDaoManager;

    private Map<String, Pair<Table<?>, LookupDao>> lookupDaos;

    public MapperFactory(Table<Record> table, LookupDaoManager lookupDaoManager) {
        this.table = table;
        this.lookupDaoManager = lookupDaoManager;
    }

    public SliceMapper createSliceMapper(SliceRequest sliceRequest) {
        return new SliceMapper(sliceRequest);
    }

    public <T> TransportUnMapper<T> createTransportUnMapper(Class<T> transportType) {
        return new TransportUnMapper<T>(transportType);
    }

    public <T> TransportMapper<T> createTransportMapper(Class<T> transportType) {
        return new TransportMapper<T>(transportType);
    }

    private Map<String, Pair<Table<?>, LookupDao>> getLookupDaos() {
        Predicate<ForeignKey<Record, ?>> singleFieldOnly = foreignKey ->
                foreignKey.getFields().size() == 1;

        Map<String, Pair<Table<?>, LookupDao>> lookupDaos = new HashMap<>();

        table.getReferences().stream()
                .filter(singleFieldOnly)
                .forEach(foreignKey -> {
                    TableField<Record, ?> field = foreignKey.getFields().get(0);
                    Table<?> table = foreignKey.getKey().getTable();
                    LookupDao lookupDao = lookupDaoManager.getDaoForTable((Table<Record>) table);

                    if (lookupDao != null) {
                        lookupDaos.putIfAbsent(field.getName(), Pair.of(table, lookupDao));
                    }
                });

        return lookupDaos;
    }

    private Pair<Table<?>, LookupDao> getLookupDao(String fieldName) {
        if (lookupDaos == null) {
            lookupDaos = getLookupDaos();
        }
        return lookupDaos.get(fieldName);
    }

    public class SliceMapper implements RecordHandler<Record> {

        private final SliceRequest sliceRequest;
        private final NamedTableMapper namedTableMapper;

        private int recordCount = 0;
        private boolean canExtend = false;

        private SliceMapper(SliceRequest sliceRequest) {
            this.sliceRequest = sliceRequest;
            this.namedTableMapper = new NamedTableMapper(sliceRequest.getSort());
        }

        @Override
        public void next(Record record) {
            if (recordCount++ < sliceRequest.getSize()) {
                namedTableMapper.next(record);
            } else {
                canExtend = true;
            }
        }

        public Slice intoSlice() {
            NamedTable table = namedTableMapper.intoTable();
            return new Slice(table, sliceRequest, canExtend);
        }
    }

    public class NamedTableMapper implements RecordHandler<Record> {

        private List<Column> columns;
        private List<Map<String, Object>> rows = new ArrayList<>();
        private Map<String, Pair<Table<?>, LookupDao>> lookupDaoMap = Maps.newHashMap();

        private NamedTableMapper(Sort sort) {
            columns = createColumns(sort);
        }

        private List<Column> createColumns(Sort sort) {
            List<TableField<Record, ?>> primaryKeyFields = getPrimaryKeyFields();
            return Stream.of(table.fields())
                    .map(field -> createColumn(primaryKeyFields, field, sort))
                    .collect(Collectors.toList());
        }

        private Column createColumn(List<TableField<Record, ?>> primaryKeyFields, org.jooq.Field<?> field, Sort sort) {

            Class<?> fieldType = field.getType();
            String fieldName = field.getName();
            Sort.Order order = sort != null ? sort.getOrderFor(field.getName()) : null;

            Pair<Table<?>, LookupDao> lookupDao = getLookupDao(fieldName);
            if (lookupDao != null) {
                fieldType = Lookup.class;
                lookupDaoMap.put(fieldName, lookupDao);
            }

            return new Column(fieldType, fieldName, primaryKeyFields.contains(field), order);
        }

        private List<TableField<Record, ?>> getPrimaryKeyFields() {
            if (table.getPrimaryKey() != null) {
                return table.getPrimaryKey().getFields();
            }
            return Collections.emptyList();
        }

        @Override
        public void next(Record record) {
            rows.add(record.intoMap());
        }

        public NamedTable intoTable() {

            if (!lookupDaoMap.isEmpty()) {

                Set<String> lookupFieldNames = lookupDaoMap.keySet();

                SetMultimap<String, Object> referenceIds = HashMultimap.create();
                rows.forEach(row -> lookupFieldNames.forEach(fieldName -> referenceIds.put(fieldName, row.get(fieldName))));

                HashBasedTable<String, Object, String> lookupLabels = fetchLookupLabels(referenceIds);

                List<Map<String, Object>> rowsWithLookups = convertRowToRowWithLookups(lookupFieldNames, lookupLabels);

                return new NamedTable(table.getName(), columns, rowsWithLookups);
            } else {
                return new NamedTable(table.getName(), columns, rows);
            }
        }

        @NotNull
        private HashBasedTable<String, Object, String> fetchLookupLabels(SetMultimap<String, Object> referenceIds) {
            HashBasedTable<String, Object, String> referenceLabels = HashBasedTable.create();
            for (String fieldName : referenceIds.keys()) {
                Pair<Table<?>, LookupDao> lookupDao = getLookupDao(fieldName);

                Map<Object, String> labels = lookupDao.getValue().fetchLabelsById(referenceIds.get(fieldName));
                labels.forEach((referenceId, label) -> referenceLabels.put(fieldName, referenceId, label));
            }
            return referenceLabels;
        }

        private List<Map<String, Object>> convertRowToRowWithLookups(Set<String> lookupFieldNames, HashBasedTable<String, Object, String> referenceLabels) {
            List<Map<String, Object>> rowsWithLookups = Lists.newArrayListWithExpectedSize(rows.size());
            for (Map<String, Object> row : rows) {
                Map<String, Object> rowWithLookups = Maps.newHashMapWithExpectedSize(row.size());
                for (Map.Entry<String, Object> entry : row.entrySet()) {
                    String fieldName = entry.getKey();
                    if (lookupFieldNames.contains(entry.getKey())) {
                        Object referenceId = entry.getValue();
                        rowWithLookups.put(fieldName, new Lookup(
                                referenceId,
                                referenceLabels.get(fieldName, referenceId),
                                lookupDaoMap.get(fieldName).getKey().getName()
                        ));
                    } else {
                        rowWithLookups.put(entry.getKey(), entry.getValue());
                    }
                }
                rowsWithLookups.add(rowWithLookups);
            }
            return rowsWithLookups;
        }
    }


    public class TransportUnMapper<T> {

        private final Map<String, Pair<Method, Class<?>>> transportGetters;

        private TransportUnMapper(Class<T> transportType) {
            this.transportGetters = getInstanceGetters(transportType);
        }

        public Record unMap(T transport, Record record) {
            if (hasLookupGetters()) {
                for (org.jooq.Field<?> field : record.fields()) {
                    // TODO Filter out all audit fields...
                    setValueToRecordField(transport, field, record);
                }
            } else {
                // TODO Filter out all audit fields...
                record.from(transport);
            }
            return record;
        }

        private void setValueToRecordField(T transport, org.jooq.Field<?> field, Record record) {
            Pair<Method, Class<?>> getter = getGetter(field);
            if (getter != null) {

                Object value = getValue(transport, getter.getKey());
                if (value instanceof Lookup) {
                    value = ((Lookup) value).getId();
                } else {
                    value = getValue(transport, getter.getKey());
                }

                if (value != null) {
                    record.setValue((org.jooq.Field<Object>) field, value);
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
            return transportGetters.values().stream().anyMatch(getter -> getter.getValue() == Lookup.class);
        }

        private Pair<Method, Class<?>> getGetter(org.jooq.Field<?> field) {
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
            Predicate<Method> nonStaticOnly = method ->
                    (method.getModifiers() & Modifier.STATIC) == 0;

            Predicate<Method> getterOnly = method -> {
                String methodName = method.getName();
                return (methodName.startsWith("get") || methodName.startsWith("is"));
            };

            return Stream.of(type.getMethods())
                    .filter(nonStaticOnly)
                    .filter(getterOnly)
                    .collect(Collectors.toMap(
                            Method::getName,
                            method -> Pair.of(method, method.getReturnType())
                    ));
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
                for (org.jooq.Field<?> field : record.fields()) {
                    setValueToTransportMember(transport, record, field);
                }
                return transport;
            } else {
                return record.into(transportType);
            }
        }

        private void setValueToTransportMember(T transport, Record record, org.jooq.Field<?> field) {
            Pair<Method, Class<?>> setter = getSetter(field);
            if (setter != null) {
                Class<?> paramType = setter.getValue();

                Object value = null;
                if (paramType == Lookup.class) {

                    Object referenceId = record.getValue(field);
                    if (referenceId != null) {
                        String fieldName = field.getName();

                        Pair<Table<?>, LookupDao> lookupDao = getLookupDao(fieldName);
                        Validate.notNull(lookupDao, "LookupDao for field " + fieldName + " was not found! Trying to set to " + transport.getClass().getSimpleName());

                        String label = lookupDao.getValue().fetchLabelById(referenceId);

                        value = new Lookup(referenceId, label, lookupDao.getKey().getName());
                    }
                } else {
                    value = record.getValue(field, paramType);
                }

                if (value != null) {
                    setValue(transport, setter.getKey(), value);
                }
            }
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
            return transportSetters.values().stream().anyMatch(setter -> setter.getValue() == Lookup.class);
        }

        private Pair<Method, Class<?>> getSetter(org.jooq.Field<?> field) {
            String fieldName = field.getName();
            String setterName = "set" + LOWER_UNDERSCORE.to(UPPER_CAMEL, fieldName.toLowerCase());
            return transportSetters.get(setterName);
        }

        private Map<String, Pair<Method, Class<?>>> getInstanceSetters(Class<?> type) {
            Predicate<Method> nonStaticOnly = method ->
                    (method.getModifiers() & Modifier.STATIC) == 0;

            Predicate<Method> setterOnly = method -> {
                String methodName = method.getName();
                return methodName.startsWith("set") && method.getParameterCount() == 1;
            };

            return Stream.of(type.getMethods())
                    .filter(nonStaticOnly)
                    .filter(setterOnly)
                    .collect(Collectors.toMap(
                            Method::getName,
                            method -> Pair.of(method, method.getParameterTypes()[0])
                    ));
        }
    }
}
