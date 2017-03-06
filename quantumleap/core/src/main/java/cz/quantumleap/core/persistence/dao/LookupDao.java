package cz.quantumleap.core.persistence.dao;

import org.jooq.Record;
import org.jooq.Table;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface LookupDao {

    Table<Record> getTable();

    String fetchLabelById(Object id);

    Map<Object, String> fetchLabelsById(Set<Object> ids);
}
