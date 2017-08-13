package cz.quantumleap.core.data.transport;

import org.springframework.data.domain.Sort;

import java.util.Iterator;
import java.util.List;

public class Table<ROW> implements Iterable<ROW> {

    private final String databaseTableNameWithSchema;
    private final List<Column> columns;
    private final List<ROW> rows;

    public Table(String databaseTableNameWithSchema, List<Column> columns, List<ROW> rows) {
        this.databaseTableNameWithSchema = databaseTableNameWithSchema;
        this.columns = columns;
        this.rows = rows;
    }

    public String getDatabaseTableNameWithSchema() {
        return databaseTableNameWithSchema;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public List<ROW> getRows() {
        return rows;
    }

    @Override
    public Iterator<ROW> iterator() {
        return rows.iterator();
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

        private final String databaseTableNameWithSchema;

        public LookupColumn(Class<?> type, String name, boolean primaryKey, Sort.Order order, String databaseTableNameWithSchema) {
            super(type, name, primaryKey, order);
            this.databaseTableNameWithSchema = databaseTableNameWithSchema;
        }

        public String getDatabaseTableNameWithSchema() {
            return databaseTableNameWithSchema;
        }
    }
}
