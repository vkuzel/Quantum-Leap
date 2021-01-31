package cz.quantumleap.core.data.transport;

import cz.quantumleap.core.data.entity.EntityIdentifier;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class Table<ROW> implements Iterable<ROW> {

    private final EntityIdentifier<?> entityIdentifier;
    private final List<Column> columns;
    private final List<ROW> rows;
    private final TablePreferences tablePreferences;

    public Table(EntityIdentifier<?> entityIdentifier, List<Column> columns, List<ROW> rows, TablePreferences tablePreferences) {
        this.entityIdentifier = entityIdentifier;
        this.columns = columns;
        this.rows = rows;
        this.tablePreferences = tablePreferences;
    }

    public Builder<ROW> createBuilder() {
        return new Builder<>(entityIdentifier, columns, rows, tablePreferences);
    }

    public EntityIdentifier<?> getEntityIdentifier() {
        return entityIdentifier;
    }

    public List<Column> getColumns() {
        List<String> enabledColumns = tablePreferences.getEnabledColumns();
        if (enabledColumns.isEmpty()) {
            return columns;
        }
        return columns.stream()
                .filter(column -> enabledColumns.contains(column.name))
                .sorted(Comparator.comparingLong(o -> enabledColumns.indexOf(o.name)))
                .collect(Collectors.toList());
    }

    public Column getColumnByName(String name) {
        for (Column column : columns) {
            if (column.getName().equals(name)) {
                return column;
            }
        }
        throw new IllegalArgumentException("Column " + name + " not found!");
    }

    public List<ROW> getRows() {
        return rows;
    }

    @NotNull
    @Override
    public Iterator<ROW> iterator() {
        return rows.iterator();
    }

    public boolean isEmpty() {
        return rows.isEmpty();
    }

    public static class Column {

        private final Class<?> type;
        private final String name;
        private final boolean primaryKey;
        private final Sort.Order order;

        public Column(Class<?> type, String name, boolean primaryKey, Sort.Order order) {
            this.type = type;
            this.name = name;
            this.primaryKey = primaryKey;
            this.order = order;
        }

        public boolean isLookupColumn() {
            return this instanceof LookupColumn;
        }

        public LookupColumn asLookupColumn() {
            return (LookupColumn) this;
        }

        public Class<?> getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public boolean isPrimaryKey() {
            return primaryKey;
        }

        public Sort.Order getOrder() {
            return order;
        }
    }

    public static class LookupColumn extends Column {

        private final EntityIdentifier<?> entityIdentifier;

        public LookupColumn(String name, Sort.Order order, EntityIdentifier<?> entityIdentifier) {
            super(Lookup.class, name, false, order);
            this.entityIdentifier = entityIdentifier;
        }

        public EntityIdentifier<?> getEntityIdentifier() {
            return entityIdentifier;
        }
    }

    public static class Builder<ROW> {

        private EntityIdentifier<?> entityIdentifier;
        private List<Column> columns;
        private List<ROW> rows;
        private TablePreferences tablePreferences;

        public Builder(EntityIdentifier<?> entityIdentifier, List<Column> columns, List<ROW> rows, TablePreferences tablePreferences) {
            this.entityIdentifier = entityIdentifier;
            this.columns = new ArrayList<>(columns);
            this.rows = new ArrayList<>(rows);
            this.tablePreferences = tablePreferences;
        }

        public Builder<ROW> addColumn(Column column, List<Object> values, BiFunction<ROW, Object, ROW> mergeRow) {
            columns.add(column);
            for (int i = 0; i < this.rows.size(); i++) {
                this.rows.set(i, mergeRow.apply(this.rows.get(i), values.get(i)));
            }
            return this;
        }

        public Table<ROW> build() {
            return new Table<>(
                    entityIdentifier,
                    columns,
                    rows,
                    tablePreferences
            );
        }
    }
}
