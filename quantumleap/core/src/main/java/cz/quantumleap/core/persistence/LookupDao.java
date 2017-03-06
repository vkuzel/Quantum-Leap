package cz.quantumleap.core.persistence;

import org.jooq.Record;
import org.jooq.Table;

import java.util.Map;
import java.util.Set;

public interface LookupDao<TABLE extends Table<? extends Record>> {

    TABLE getTable();

    String fetchLabelById(Object id);

    Map<Object, String> fetchLabelsById(Set<Object> ids);
}
