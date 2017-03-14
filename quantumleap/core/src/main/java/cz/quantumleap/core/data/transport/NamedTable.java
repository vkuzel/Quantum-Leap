package cz.quantumleap.core.data.transport;

import org.springframework.data.domain.Sort;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

// Actually, this class has name NamedTable because of name collision with jooq.Table
public class NamedTable implements Iterable<Map<NamedTable.Column, Object>> {

    private final String name;
    private final List<Column> columns;
    private final List<Map<Column, Object>> rows;

    public NamedTable(String name, List<Column> columns, List<Map<Column, Object>> rows) {
        this.name = name;
        this.columns = columns;
        this.rows = rows;
    }

    public String getName() {
        return name;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public List<Map<Column, Object>> getRows() {
        return rows;
    }

    @Override
    public Iterator<Map<Column, Object>> iterator() {
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
}
