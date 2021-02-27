package cz.quantumleap.core.business;

import cz.quantumleap.core.database.entity.EntityIdentifier;
import org.jooq.Record;
import org.jooq.Table;

import java.util.Map;

public interface LookupService extends ListService {

    <TABLE extends Table<? extends Record>> EntityIdentifier<?> getLookupEntityIdentifier(Class<TABLE> type);

    String findLookupLabel(Object id);

    Map<Object, String> findLookupLabels(String query);
}
