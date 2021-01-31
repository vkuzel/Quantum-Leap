package cz.quantumleap.core.data.mapper;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;
import cz.quantumleap.core.data.EnumManager;
import cz.quantumleap.core.data.LookupDaoManager;
import cz.quantumleap.core.data.entity.Entity;
import cz.quantumleap.core.data.entity.EntityIdentifier;
import cz.quantumleap.core.data.transport.Table;
import cz.quantumleap.core.data.transport.Table.Column;
import cz.quantumleap.core.data.transport.Table.LookupColumn;
import cz.quantumleap.core.data.transport.TablePreferences;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.RecordHandler;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;

public class TableMapper<TABLE extends org.jooq.Table<? extends Record>> implements RecordHandler<Record> {

    private final Entity<TABLE> entity;

    private final TableColumnHandler tableColumnHandler;
    private final List<Column> columns;
    private final List<Map<Column, Object>> rows;
    private final SetMultimap<LookupColumn, Object> lookupReferenceIds = HashMultimap.create();

    TableMapper(Entity<TABLE> entity, Sort sort, int expectedSize) {
        this.entity = entity;
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
        }
        rows.add(row);
    }

    public Table<Map<Column, Object>> intoTable(TablePreferences tablePreferences) {
        return new Table<>(entity.getIdentifier(), columns, rows, tablePreferences);
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

        private void buildFieldColumnMap() {
            List<Field<?>> primaryKeyFields = entity.getPrimaryKeyFields();
            Field<?>[] fields = entity.getTable().fields();

            for (Field<?> field : fields) {
                String name = field.getName();
                Sort.Order order = sort != null ? sort.getOrderFor(field.getName()) : null;

                Column column = new Column(
                        field.getType(),
                        name,
                        primaryKeyFields.contains(field),
                        order
                );
                fieldColumnMap.put(field, column);
            }
        }
    }
}
