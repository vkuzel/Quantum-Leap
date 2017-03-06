package cz.quantumleap.core.persistence.dao;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Table;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultLookupDao implements LookupDao {

    protected final Table<? extends Record> table;
    protected final DSLContext dslContext;

    public DefaultLookupDao(Table<? extends Record> table, DSLContext dslContext) {
        this.table = table;
        this.dslContext = dslContext;
    }

    public Table<? extends Record> getTable() {
        return table;
    }
// TODO Specify label columns in constructor...
    public String fetchLabelById(Object id) {
        return "xxx: " + id;
    }

    public Map<Object, String> fetchLabelsById(Set<Object> ids) {
        return ids.stream().collect(Collectors.toMap(id -> id, id -> "xxx: " + id));
    }
}
