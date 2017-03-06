package cz.quantumleap.core.persistence.transport;

import org.springframework.data.domain.Sort;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

// Actually, this class has name NamedTable because of name collision with jooq.Table
public class NamedTable implements Iterable<Map<String, Object>> {

    private final String name;
    private final List<Column> columns;
    // TODO Re-do this to Map<Column, Object>
    private final List<Map<String, Object>> rows;

    public NamedTable(String name, List<Column> columns, List<Map<String, Object>> rows) {
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

    public List<Map<String, Object>> getRows() {
        return rows;
    }

    @Override
    public Iterator<Map<String, Object>> iterator() {
        return rows.iterator();
    }

    public static class Column {

        private final Class<?> type;
        private final String name;
        private final boolean identifier;
        private final Sort.Order order;

        public Column(Class<?> type, String name, boolean identifier, Sort.Order order) {
            this.type = type;
            this.name = name;
            this.identifier = identifier;
            this.order = order;
        }

        public Class<?> getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public boolean isIdentifier() {
            return identifier;
        }

        public Sort.Order getOrder() {
            return order;
        }
    }
}
