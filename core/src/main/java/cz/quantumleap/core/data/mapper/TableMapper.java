package cz.quantumleap.core.data.mapper;

import com.google.common.collect.*;
import cz.quantumleap.core.data.EnumManager;
import cz.quantumleap.core.data.LookupDao;
import cz.quantumleap.core.data.LookupDaoManager;
import cz.quantumleap.core.data.entity.Entity;
import cz.quantumleap.core.data.entity.EntityIdentifier;
import cz.quantumleap.core.data.transport.Lookup;
import cz.quantumleap.core.data.transport.Table;
import cz.quantumleap.core.data.transport.Table.Column;
import cz.quantumleap.core.data.transport.Table.LookupColumn;
import cz.quantumleap.core.data.transport.TablePreferences;
import org.jetbrains.annotations.NotNull;
import org.jooq.*;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableMapper<TABLE extends org.jooq.Table<? extends Record>> implements RecordHandler<Record> {

    private final Entity<TABLE> entity;
    private final LookupDaoManager lookupDaoManager;
    private final EnumManager enumManager;

    private final TableColumnHandler tableColumnHandler;
    private final List<Column> columns;
    private final List<Map<Column, Object>> rows;
    private final SetMultimap<LookupColumn, Object> lookupReferenceIds = HashMultimap.create();

    TableMapper(Entity<TABLE> entity, LookupDaoManager lookupDaoManager, EnumManager enumManager, Sort sort, int expectedSize) {
        this.entity = entity;
        this.lookupDaoManager = lookupDaoManager;
        this.enumManager = enumManager;
        this.tableColumnHandler = new TableColumnHandler(entity.getIdentifier(), sort);
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
            }
        }
        rows.add(row);
    }

    public Table<Map<Column, Object>> intoTable(TablePreferences tablePreferences) {
        if (tableColumnHandler.hasComplexColumns()) {
            HashBasedTable<Object, Column, String> lookupLabels = fetchLookupLabels();
            List<Map<Column, Object>> rowsWithLookups = convertRowsToRowsWithComplexValues(lookupLabels);
            return new Table<>(entity.getIdentifier(), columns, rowsWithLookups, tablePreferences);
        } else {
            return new Table<>(entity.getIdentifier(), columns, rows, tablePreferences);
        }
    }

    @NotNull
    private HashBasedTable<Object, Column, String> fetchLookupLabels() {
        HashBasedTable<Object, Column, String> valueColumnLabels = HashBasedTable.create();
        for (LookupColumn lookupColumn : lookupReferenceIds.keys()) {
            EntityIdentifier<?> entityIdentifier = lookupColumn.getEntityIdentifier();
            LookupDao<?> lookupDao = lookupDaoManager.getDaoByEntityIdentifier(entityIdentifier);
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
                    rowWithComplexValues.put(column, new Lookup<>(
                            value,
                            lookupLabels.get(value, column),
                            lookupColumn.getEntityIdentifier()
                    ));
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

        private TableColumnHandler(EntityIdentifier<?> entityIdentifier, Sort sort) {
            this.sort = sort;
            this.fieldColumnMap = Maps.newLinkedHashMapWithExpectedSize(entityIdentifier.getTable().fields().length);

            buildFieldColumnMap();
        }

        private List<Column> getColumns() {
            return Lists.newArrayList(fieldColumnMap.values());
        }

        private boolean hasComplexColumns() {
            for (Column column : fieldColumnMap.values()) {
                if (column.isLookupColumn()) {
                    return true;
                }
            }
            return false;
        }

        private void buildFieldColumnMap() {
            List<Field<?>> primaryKeyFields = entity.getPrimaryKeyFields();
            Map<Field<?>, EntityIdentifier<?>> lookupIdentifierMap = getLookupIdentifierMap();
            Field<?>[] fields = entity.getTable().fields();

            for (Field<?> field : fields) {
                String name = field.getName();
                Sort.Order order = sort != null ? sort.getOrderFor(field.getName()) : null;

                Column column;
                if (lookupIdentifierMap.containsKey(field)) {
                    column = new Table.LookupColumn(
                            name,
                            order,
                            lookupIdentifierMap.get(field)
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

        private Map<Field<?>, EntityIdentifier<?>> getLookupIdentifierMap() {
            Map<Field<?>, EntityIdentifier<?>> map = new HashMap<>();

            for (ForeignKey<? extends Record, ?> foreignKey : entity.getTable().getReferences()) {
                if (foreignKey.getFieldsArray().length != 1) {
                    continue;
                }

                TableField<? extends Record, ?> tableField = foreignKey.getFieldsArray()[0];
                EntityIdentifier<?> defaultEntityIdentifier = EntityIdentifier.forTable(foreignKey.getKey().getTable());
                EntityIdentifier<?> entityIdentifier = entity.getLookupFieldsMap().getOrDefault(tableField, defaultEntityIdentifier);

                if (lookupDaoManager.getDaoByEntityIdentifier(entityIdentifier) != null) {
                    map.put(tableField, entityIdentifier);
                }
            }
            return map;
        }
    }
}
