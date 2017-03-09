package cz.quantumleap.core.data;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Table;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class DefaultLookupDao<TABLE extends Table<? extends Record>> implements LookupDao<TABLE> {

    private final TABLE table;
    private final DSLContext dslContext;

    public DefaultLookupDao(TABLE table, DSLContext dslContext) {
        this.table = table;
        this.dslContext = dslContext;
    }

    public TABLE getTable() {
        return table;
    }
// TODO Specify label columns in constructor...
    public String fetchLabelById(Object id) {
        return "xxx: " + id;
    }

    public Map<Object, String> fetchLabelsById(Set<Object> ids) {
        return ids.stream().collect(Collectors.toMap(id -> id, id -> "xxx: " + id));
    }

    @Override
    public Map<Object, String> fetchLabelsByFilter(String filter) {
        throw new UnsupportedOperationException();
    }
}
