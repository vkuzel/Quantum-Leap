package cz.quantumleap.core.data;

import cz.quantumleap.core.data.entity.EntityIdentifier;
import org.jooq.Record;
import org.jooq.Table;

import java.util.Map;
import java.util.Set;

public interface LookupDao<TABLE extends Table<? extends Record>> extends ListDao<TABLE> {

    EntityIdentifier getEntityIdentifier();

    String fetchLabelById(Object id);

    Map<Object, String> fetchLabelsById(Set<Object> ids);

    Map<Object, String> fetchLabelsByFilter(String query);
}
