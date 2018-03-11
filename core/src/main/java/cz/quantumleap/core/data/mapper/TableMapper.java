package cz.quantumleap.core.data.mapper;

import com.google.common.collect.*;
import cz.quantumleap.core.data.EnumManager;
import cz.quantumleap.core.data.LookupDao;
import cz.quantumleap.core.data.LookupDaoManager;
import cz.quantumleap.core.data.primarykey.PrimaryKeyResolver;
import cz.quantumleap.core.data.transport.Lookup;
import cz.quantumleap.core.data.transport.Table;
import cz.quantumleap.core.data.transport.Table.Column;
import cz.quantumleap.core.data.transport.Table.EnumColumn;
import cz.quantumleap.core.data.transport.Table.LookupColumn;
import cz.quantumleap.core.data.transport.Table.SetColumn;
import cz.quantumleap.core.data.transport.TablePreferences;
import org.jetbrains.annotations.NotNull;
import org.jooq.*;
import org.springframework.data.domain.Sort;

import java.util.*;

import cz.quantumleap.core.data.transport.Table;

public class TableMapper implements RecordHandler<Record> {

    private final org.jooq.Table<? extends Record> table;
    private final PrimaryKeyResolver primaryKeyResolver;
    private final LookupDaoManager lookupDaoManager;
    private final EnumManager enumManager;

    private final TableColumnHandler tableColumnHandler;
    private final List<Column> columns;
    private final List<Map<Column, Object>> rows;
    private final SetMultimap<LookupColumn, Object> lookupReferenceIds = HashMultimap.create();
    private final SetMultimap<EnumColumn, Object> enumReferenceIds = HashMultimap.create();

    TableMapper(org.jooq.Table<? extends Record> table, PrimaryKeyResolver primaryKeyResolver, LookupDaoManager lookupDaoManager, EnumManager enumManager, Sort sort, int expectedSize) {
        this.table = table;
        this.primaryKeyResolver = primaryKeyResolver;
        this.lookupDaoManager = lookupDaoManager;
        this.enumManager = enumManager;
        this.tableColumnHandler = new TableColumnHandler(table, sort);
        this.columns = tableColumnHandler.getColumns();
        this.rows = Lists.newArrayListWithExpectedSize(expectedSize);
    }

    @Override
    public void next(Record record) {
        Object[] values = record.intoArray();
        Map<Column, Object> row = Maps.newHashMapWithExpectedSize(values.length);
        for (int i = 0; i < values.length; i++) {
            Column column = columns.get(i);
            Object value = values[i];

            row.put(column, value);

            if (column.isLookupColumn()) {
                lookupReferenceIds.put(column.asLookupColumn(), value);
            } else if (column.isEnumColumn() || column.isSetColumn()) {
                enumReferenceIds.put(column.asEnumColumn(), value);
            }
        }
        rows.add(row);
    }

    public Table<Map<Column, Object>> intoTable(TablePreferences tablePreferences) {
        if (tableColumnHandler.hasComplexColumns()) {
            HashBasedTable<Object, Column, String> lookupLabels = fetchLookupLabels();
            List<Map<Column, Object>> rowsWithLookups = convertRowsToRowsWithComplexValues(lookupLabels);
            return new Table<>(MapperUtils.resolveDatabaseTableNameWithSchema(table), columns, rowsWithLookups, tablePreferences);
        } else {
            return new Table<>(MapperUtils.resolveDatabaseTableNameWithSchema(table), columns, rows, tablePreferences);
        }
    }

    @NotNull
    private HashBasedTable<Object, Column, String> fetchLookupLabels() {
        HashBasedTable<Object, Column, String> valueColumnLabels = HashBasedTable.create();
        for (LookupColumn lookupColumn : lookupReferenceIds.keys()) {
            String databaseTableNameWithSchema = lookupColumn.getDatabaseTableNameWithSchema();
            LookupDao<org.jooq.Table<? extends Record>> lookupDao = lookupDaoManager.getDaoByDatabaseTableNameWithSchema(databaseTableNameWithSchema);
            Map<Object, String> labels = lookupDao.fetchLabelsById(lookupReferenceIds.get(lookupColumn));
            labels.forEach((referenceId, label) -> valueColumnLabels.put(referenceId, lookupColumn, label));
        }
        return valueColumnLabels;
    }

    private List<Map<Column, Object>> convertRowsToRowsWithComplexValues(HashBasedTable<Object, Column, String> lookupLabels) {
        List<Map<Column, Object>> rowsWithComplexValues = new ArrayList<>(rows.size());
        for (Map<Column, Object> row : rows) {
            Map<Column, Object> rowWithComplexValues = Maps.newHashMapWithExpectedSize(row.size());
            row.forEach((column, value) -> {
                if (column.isLookupColumn()) {
                    LookupColumn lookupColumn = column.asLookupColumn();
                    rowWithComplexValues.put(column, new Lookup(
                            value,
                            lookupLabels.get(value, column),
                            lookupColumn.getDatabaseTableNameWithSchema()
                    ));
                } else if (column.isEnumColumn()) {
                    EnumColumn enumColumn = column.asEnumColumn();
                    rowWithComplexValues.put(column, enumManager.createEnumValue(enumColumn.getEnumId(), String.valueOf(value)));
                } else if (column.isSetColumn()) {
                    SetColumn setColumn = column.asSetColumn();
                    rowWithComplexValues.put(column, enumManager.createSet(setColumn.getEnumId(), Arrays.asList((String[]) value)));
                } else {
                    rowWithComplexValues.put(column, value);
                }
            });
            rowsWithComplexValues.add(rowWithComplexValues);
        }
        return rowsWithComplexValues;
    }

    private class TableColumnHandler {

        private final Sort sort;

        private final Map<Field<?>, Column> fieldColumnMap;

        private TableColumnHandler(org.jooq.Table<? extends Record> table, Sort sort) {
            this.sort = sort;
            this.fieldColumnMap = Maps.newLinkedHashMapWithExpectedSize(table.fields().length);

            buildFieldColumnMap();
        }

        private List<Column> getColumns() {
            return Lists.newArrayList(fieldColumnMap.values());
        }

        private boolean hasComplexColumns() {
            for (Column column : fieldColumnMap.values()) {
                if (column.isLookupColumn() || column.isEnumColumn() || column.isSetColumn()) {
                    return true;
                }
            }
            return false;
        }

        private void buildFieldColumnMap() {
            List<Field<Object>> primaryKeyFields = primaryKeyResolver.getPrimaryKeyFields();
            Map<Field<?>, String> lookupFieldDatabaseTableNameWithSchemaMap = getLookupFieldDatabaseTableNameWithSchemaMap();

            for (Field<?> field : table.fields()) {
                String name = field.getName();
                Sort.Order order = sort != null ? sort.getOrderFor(field.getName()) : null;

                Column column;
                if (lookupFieldDatabaseTableNameWithSchemaMap.containsKey(field)) {
                    column = new Table.LookupColumn(
                            name,
                            order,
                            lookupFieldDatabaseTableNameWithSchemaMap.get(field)
                    );
                } else if (isEnumField(field)) {
                    column = new EnumColumn(
                            name,
                            order,
                            MapperUtils.resolveEnumId(field)
                    );
                } else if (isSetField(field)) {
                    column = new SetColumn(
                            name,
                            order,
                            MapperUtils.resolveEnumId(field)
                    );
                } else {
                    column = new Column(
                            field.getType(),
                            name,
                            primaryKeyFields.contains(field),
                            order
                    );
                }
                fieldColumnMap.put(field, column);
            }
        }

        private Map<Field<?>, String> getLookupFieldDatabaseTableNameWithSchemaMap() {
            Map<Field<?>, String> map = new HashMap<>();
            for (ForeignKey<? extends Record, ?> foreignKey : table.getReferences()) {
                if (foreignKey.getFieldsArray().length != 1) {
                    continue;
                }

                TableField<? extends Record, ?> tableField = foreignKey.getFieldsArray()[0];
                String databaseTableNameWithSchema = MapperUtils.resolveDatabaseTableNameWithSchema(foreignKey.getKey().getTable());

                if (lookupDaoManager.getDaoByDatabaseTableNameWithSchema(databaseTableNameWithSchema) != null) {
                    map.put(tableField, databaseTableNameWithSchema);
                }
            }
            return map;
        }

        private boolean isEnumField(Field<?> field) {
            String enumId = MapperUtils.resolveEnumId(field);
            return enumManager.isEnum(enumId) && field.getDataType().isString();
        }

        private boolean isSetField(Field<?> field) {
            String enumId = MapperUtils.resolveEnumId(field);
            return enumManager.isEnum(enumId) && field.getDataType().isArray();
        }
    }
}
