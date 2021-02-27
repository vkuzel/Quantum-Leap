package cz.quantumleap.core.database;

import cz.quantumleap.core.database.domain.SliceRequest;
import cz.quantumleap.core.database.domain.TableSlice;
import cz.quantumleap.core.database.entity.Entity;
import cz.quantumleap.core.database.entity.EntityIdentifier;
import org.jooq.Record;
import org.jooq.Table;

import java.util.Map;
import java.util.Set;

public interface LookupDao<TABLE extends Table<? extends Record>> {

    /**
     * LookupDao can return an entity different from an entity of a TABLE. This
     * will allow to register a lookup DAO based on an view for a table.
     */
    Entity<?> getLookupEntity();

    default EntityIdentifier<?> getLookupEntityIdentifier() {
        return getLookupEntity().getIdentifier();
    }

    String fetchLabelById(Object id);

    Map<Object, String> fetchLabelsById(Set<Object> ids);

    Map<Object, String> fetchLabelsByFilter(String query);

    TableSlice fetchTableSlice(SliceRequest sliceRequest);
}