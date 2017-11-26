package cz.quantumleap.core.data.mapper;

import com.google.common.collect.*;
import cz.quantumleap.core.data.LookupDao;
import cz.quantumleap.core.data.LookupDaoManager;
import cz.quantumleap.core.data.transport.Lookup;
import cz.quantumleap.core.data.transport.Table;
import cz.quantumleap.core.data.transport.Table.Column;
import cz.quantumleap.core.data.transport.Table.LookupColumn;
import cz.quantumleap.core.data.transport.TablePreferences;
import org.jetbrains.annotations.NotNull;
import org.jooq.*;
import org.springframework.data.domain.Sort;

import java.util.*;

public class TableMapper implements RecordHandler<Record> {

    private final org.jooq.Table<? extends Record> table;
    private final LookupDaoManager lookupDaoManager;

    private final TableColumnHandler tableColumnHandler;
    private final List<Column> columns;
    private final List<Map<Column, Object>> rows;
    private final SetMultimap<LookupColumn, Object> referenceIds = HashMultimap.create();

    TableMapper(org.jooq.Table<? extends Record> table, LookupDaoManager lookupDaoManager, Sort sort, int expectedSize) {
        this.table = table;
        this.lookupDaoManager = lookupDaoManager;
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
                referenceIds.put(column.asLookupColumn(), value);
            }
        }
        rows.add(row);
    }

    public Table<Map<Column, Object>> intoTable(TablePreferences tablePreferences) {
        if (tableColumnHandler.hasLookupColumns()) {
            HashBasedTable<Object, Column, String> lookupLabels = fetchLookupLabels();
            List<Map<Column, Object>> rowsWithLookups = convertRowsToRowsWithLookups(lookupLabels);
            return new Table<>(MapperUtils.resolveDatabaseTableNameWithSchema(table), columns, rowsWithLookups, tablePreferences);
        } else {
            return new Table<>(MapperUtils.resolveDatabaseTableNameWithSchema(table), columns, rows, tablePreferences);
        }
    }

    @NotNull
    private HashBasedTable<Object, Column, String> fetchLookupLabels() {
        HashBasedTable<Object, Column, String> referenceLabels = HashBasedTable.create();
        for (LookupColumn lookupColumn : referenceIds.keys()) {
            String databaseTableNameWithSchema = lookupColumn.getDatabaseTableNameWithSchema();
            LookupDao<org.jooq.Table<? extends Record>> lookupDao = lookupDaoManager.getDaoByDatabaseTableNameWithSchema(databaseTableNameWithSchema);
            Map<Object, String> labels = lookupDao.fetchLabelsById(referenceIds.get(lookupColumn));
            labels.forEach((referenceId, label) -> referenceLabels.put(referenceId, lookupColumn, label));
        }
        return referenceLabels;
    }

    private List<Map<Column, Object>> convertRowsToRowsWithLookups(HashBasedTable<Object, Column, String> lookupLabels) {
        List<Map<Column, Object>> rowsWithLookups = new ArrayList<>(rows.size());
        for (Map<Column, Object> row : rows) {
            Map<Column, Object> rowWithLookups = Maps.newHashMapWithExpectedSize(row.size());
            row.forEach((column, value) -> {
                if (column.isLookupColumn()) {
                    LookupColumn lookupColumn = column.asLookupColumn();
                    rowWithLookups.put(column, new Lookup(
                            value,
                            lookupLabels.get(value, column),
                            lookupColumn.getDatabaseTableNameWithSchema()
                    ));
                } else {
                    rowWithLookups.put(column, value);
                }
            });
            rowsWithLookups.add(rowWithLookups);
        }
        return rowsWithLookups;
    }

    private class TableColumnHandler {

        private final Sort sort;

        private final Map<Field<?>, Column> fieldColumnMap;

        private TableColumnHandler(org.jooq.Table<? extends Record> table, Sort sort) {
            this.sort = sort;
            this.fieldColumnMap = Maps.newLinkedHashMapWithExpectedSize(table.fields().length);

            createColumns();
        }

        private List<Column> getColumns() {
            return Lists.newArrayList(fieldColumnMap.values());
        }

        private boolean hasLookupColumns() {
            for (Column column : fieldColumnMap.values()) {
                if (column.getType() == Lookup.class) {
                    return true;
                }
            }
            return false;
        }

        private void createColumns() {
            List<Field<?>> primaryKeyFields = getPrimaryKeyFields();
            Map<Field<?>, String> lookupFieldDatabaseTableNameWithSchemaMap = getLookupFieldDatabaseTableNameWithSchemaMap();

            for (Field<?> field : table.fields()) {
                String name = field.getName();
                Sort.Order order = sort != null ? sort.getOrderFor(field.getName()) : null;

                Column column;
                if (lookupFieldDatabaseTableNameWithSchemaMap.containsKey(field)) {
                    column = new Table.LookupColumn(
                            Lookup.class,
                            name,
                            false,
                            order,
                            lookupFieldDatabaseTableNameWithSchemaMap.get(field)
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

        private List<Field<?>> getPrimaryKeyFields() {
            if (table.getPrimaryKey() != null) {
                TableField<? extends Record, ?>[] tableFields = table.getPrimaryKey().getFieldsArray();
                List<Field<?>> fields = new ArrayList<>(tableFields.length);

                for (TableField<? extends Record, ?> tableField : tableFields) {
                    Field<?> field = table.field(tableField.getName());
                    fields.add(field);
                }

                return fields;
            }
            return Collections.emptyList();
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
    }
}
