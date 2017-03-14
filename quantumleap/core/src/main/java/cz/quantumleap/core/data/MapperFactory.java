package cz.quantumleap.core.data;

import com.google.common.collect.*;
import cz.quantumleap.core.data.transport.Lookup;
import cz.quantumleap.core.data.transport.NamedTable;
import cz.quantumleap.core.data.transport.NamedTable.Column;
import cz.quantumleap.core.data.transport.Slice;
import cz.quantumleap.core.data.transport.SliceRequest;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jooq.*;
import org.jooq.Table;
import org.springframework.data.domain.Sort;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;

public class MapperFactory {

    private final Table<? extends Record> table;
    private final LookupDaoManager lookupDaoManager;

    private Map<String, TableLookupDao> lookupDaos;

    public MapperFactory(Table<? extends Record> table, LookupDaoManager lookupDaoManager) {
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

    private Map<String, TableLookupDao> getLookupDaos() {
        Predicate<ForeignKey<? extends Record, ?>> singleFieldOnly = foreignKey ->
                foreignKey.getFields().size() == 1;

        Map<String, TableLookupDao> lookupDaos = new HashMap<>();

        table.getReferences().stream()
                .filter(singleFieldOnly)
                .forEach(foreignKey -> {
                    TableField<? extends Record, ?> field = foreignKey.getFields().get(0);
                    Table<?> table = foreignKey.getKey().getTable();
                    LookupDao<Table<? extends Record>> lookupDao = lookupDaoManager.getDaoForTable(table);

                    if (lookupDao != null) {
                        lookupDaos.put(field.getName(), new TableLookupDao(table, lookupDao));
                    }
                });

        return lookupDaos;
    }

    private static class TableLookupDao {

        private final Table<?> table;
        private final LookupDao<Table<? extends Record>> dao;

        private TableLookupDao(Table<?> table, LookupDao<Table<? extends Record>> dao) {
            this.table = table;
            this.dao = dao;
        }

        private LookupDao<Table<? extends Record>> getDao() {
            return dao;
        }

        private String getDatabaseTableNameWithSchema() {
            String name = table.getName();
            if (table.getSchema() != null) {
                name = table.getSchema().getName() + "." + name;
            }
            return name;
        }
    }

    private TableLookupDao getTableLookupDao(String fieldName) {
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
            this.namedTableMapper = new NamedTableMapper(sliceRequest.getSort(), sliceRequest.getSize());
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

        private final List<Column> columns;
        private final List<Map<Column, Object>> rows;

        private final SetMultimap<Column, Object> referenceIds = HashMultimap.create();

        private final Map<String, TableLookupDao> lookupDaoMap = Maps.newHashMap();

        private NamedTableMapper(Sort sort, int expectedSize) {
            columns = createColumns(sort);
            rows = Lists.newArrayListWithExpectedSize(expectedSize);
        }

        private List<Column> createColumns(Sort sort) {

            List<? extends TableField<? extends Record, ?>> primaryKeyFields = getPrimaryKeyFields();

            return Stream.of(table.fields())
                    .map(field -> createColumn(field, primaryKeyFields.contains(field), sort))
                    .collect(Collectors.toList());
        }

        private Column createColumn(Field<?> field, boolean primaryKey, Sort sort) {
            Class<?> fieldType = field.getType();
            String fieldName = field.getName();
            Sort.Order order = sort != null ? sort.getOrderFor(field.getName()) : null;

            TableLookupDao tableLookupDao = getTableLookupDao(fieldName);
            if (tableLookupDao != null) {
                fieldType = Lookup.class;
                lookupDaoMap.put(fieldName, tableLookupDao);
            }

            return new Column(fieldType, fieldName, primaryKey, order);
        }

        private List<? extends TableField<? extends Record, ?>> getPrimaryKeyFields() {
            if (table.getPrimaryKey() != null) {
                return table.getPrimaryKey().getFields();
            }
            return Collections.emptyList();
        }

        @Override
        public void next(Record record) {

            Object[] values = record.intoArray();

            Map<Column, Object> row = Maps.newHashMapWithExpectedSize(values.length);

            for (int i = 0; i < values.length; i++) {
                Column column = columns.get(i);
                Object value = values[i];

                row.put(column, value);

                if (column.getType() == Lookup.class) {
                    referenceIds.put(column, value);
                }
            }

            rows.add(row);
        }

        public NamedTable intoTable() {

            if (!lookupDaoMap.isEmpty()) {

                HashBasedTable<Object, Column, String> lookupLabels = fetchLookupLabels();
                List<Map<Column, Object>> rowsWithLookups = convertRowsToRowsWithLookups(lookupLabels);

                return new NamedTable(table.getName(), columns, rowsWithLookups);
            } else {
                return new NamedTable(table.getName(), columns, rows);
            }
        }

        @NotNull
        private HashBasedTable<Object, Column, String> fetchLookupLabels() {

            HashBasedTable<Object, Column, String> referenceLabels = HashBasedTable.create();
            for (Column column : referenceIds.keys()) {
                // TODO Revisit this getTableLookupDao by name...
                TableLookupDao tableLookupDao = getTableLookupDao(column.getName());

                Map<Object, String> labels = tableLookupDao.getDao().fetchLabelsById(referenceIds.get(column));
                labels.forEach((referenceId, label) -> referenceLabels.put(referenceId, column, label));
            }

            return referenceLabels;
        }

        private List<Map<Column, Object>> convertRowsToRowsWithLookups(HashBasedTable<Object, Column, String> lookupLabels) {

            List<Map<Column, Object>> rowsWithLookups = Lists.newArrayListWithExpectedSize(rows.size());
            for (Map<Column, Object> row : rows) {

                Map<Column, Object> rowWithLookups = Maps.newHashMapWithExpectedSize(row.size());

                row.forEach((column, value) -> {
                    if (column.getType() == Lookup.class) {
                        rowWithLookups.put(column, new Lookup(
                                value,
                                lookupLabels.get(value, column),
                                // TODO Get rid of this column.getName()
                                lookupDaoMap.get(column.getName()).getDatabaseTableNameWithSchema()
                        ));
                    } else {
                        rowWithLookups.put(column, value);
                    }
                });

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
            return transportGetters.values().stream().anyMatch(getter -> getter.getValue() == Lookup.class);
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

                        TableLookupDao tableLookupDao = getTableLookupDao(fieldName);
                        Validate.notNull(tableLookupDao, "LookupDao for field " + fieldName + " was not found! Trying to set to " + transport.getClass().getSimpleName());

                        String label = tableLookupDao.getDao().fetchLabelById(referenceId);

                        value = new Lookup(referenceId, label, tableLookupDao.getDatabaseTableNameWithSchema());
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

        private Pair<Method, Class<?>> getSetter(Field<?> field) {
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
