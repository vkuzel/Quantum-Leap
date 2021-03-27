package cz.quantumleap.core.database.domain;

import cz.quantumleap.core.database.entity.EntityIdentifier;
import cz.quantumleap.core.slicequery.domain.SliceQuery;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;

public class Slice implements Iterable<List<Object>> {

    private final EntityIdentifier<?> entityIdentifier;
    private final FetchParams fetchParams;
    private final List<SliceQuery> sliceQueries;
    private final boolean canExtend;

    private final List<Column> columns;
    private final List<List<Object>> rows;

    public Slice(
            EntityIdentifier<?> entityIdentifier,
            FetchParams fetchParams,
            List<SliceQuery> sliceQueries,
            boolean canExtend,
            List<Column> columns,
            List<List<Object>> rows
    ) {
        this.entityIdentifier = entityIdentifier;
        this.fetchParams = fetchParams;
        this.sliceQueries = sliceQueries;
        this.canExtend = canExtend;
        this.columns = columns;
        this.rows = rows;
    }

    public Builder builder() {
        return new Builder(entityIdentifier, fetchParams, sliceQueries, canExtend, columns, rows);
    }

    public EntityIdentifier<?> getEntityIdentifier() {
        return entityIdentifier;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public Column getColumnByName(String name) {
        for (Column column : columns) {
            if (column.getName().equals(name)) {
                return column;
            }
        }
        throw new IllegalArgumentException("Column " + name + " not found!");
    }

    public List<List<Object>> getRows() {
        return rows;
    }

    public Object getValue(Column column, List<Object> row) {
        int columnIndex = columns.indexOf(column);
        return row.get(columnIndex);
    }

    public boolean isLookup(Object value) {
        return value instanceof Lookup;
    }

    @NotNull
    @Override
    public Iterator<List<Object>> iterator() {
        return rows.iterator();
    }

    public boolean isEmpty() {
        return rows.isEmpty();
    }

    public FetchParams getFetchParams() {
        return fetchParams;
    }

    public List<SliceQuery> getSliceQueries() {
        return sliceQueries;
    }

    public boolean canExtend() {
        return canExtend;
    }

    public FetchParams extend() {
        return canExtend ? fetchParams.extend() : null;
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

        public String getName() {
            return name;
        }

        public boolean isPrimaryKey() {
            return primaryKey;
        }

        public boolean isLookup() {
            return this instanceof LookupColumn;
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

    public static class Lookup {

        private final Object id;
        private final String label;
        private final EntityIdentifier<?> entityIdentifier;

        public Lookup(Object id, String label, EntityIdentifier<?> entityIdentifier) {
            this.id = id;
            this.label = label;
            this.entityIdentifier = entityIdentifier;
        }

        public Object getId() {
            return id;
        }

        public String getLabel() {
            return label;
        }

        public EntityIdentifier<?> getEntityIdentifier() {
            return entityIdentifier;
        }

        @Override
        public String toString() {
            return "Lookup{" +
                    "id=" + id +
                    ", label='" + label + '\'' +
                    ", entityIdentifier=" + entityIdentifier +
                    '}';
        }
    }

    public static class Builder {

        private final EntityIdentifier<?> entityIdentifier;
        private final FetchParams fetchParams;
        private final List<SliceQuery> sliceQueries;
        private final boolean canExtend;

        private final List<Column> columns;
        private final List<List<Object>> rows;

        private Builder(
                EntityIdentifier<?> entityIdentifier,
                FetchParams fetchParams,
                List<SliceQuery> sliceQueries,
                boolean canExtend,
                List<Column> columns,
                List<List<Object>> rows
        ) {
            this.entityIdentifier = entityIdentifier;
            this.fetchParams = fetchParams;
            this.sliceQueries = sliceQueries;
            this.canExtend = canExtend;
            this.columns = new ArrayList<>(columns);
            this.rows = new ArrayList<>(rows);
        }

        public Builder addColumn(Column column, List<Object> values, BiFunction<List<Object>, Object, List<Object>> mergeRow) {
            columns.add(column);
            for (int i = 0; i < this.rows.size(); i++) {
                this.rows.set(i, mergeRow.apply(this.rows.get(i), values.get(i)));
            }
            return this;
        }

        public Slice build() {
            return new Slice(entityIdentifier, fetchParams, sliceQueries, canExtend, columns, rows);
        }
    }
}
