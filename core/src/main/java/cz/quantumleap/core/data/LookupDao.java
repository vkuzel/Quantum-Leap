package cz.quantumleap.core.data;

import cz.quantumleap.core.data.entity.EntityIdentifier;
import cz.quantumleap.core.data.transport.Slice;
import cz.quantumleap.core.data.transport.SliceRequest;
import cz.quantumleap.core.data.transport.Table.Column;
import org.jooq.Record;
import org.jooq.Table;

import java.util.Map;
import java.util.Set;

public interface LookupDao<TABLE extends Table<? extends Record>> {

    /**
     * LookupDao can return an entity identifier different than an entity
     * identifier of a TABLE. This will allow to register a lookup DAO based on
     * an view for a table.
     */
    EntityIdentifier<?> getLookupEntityIdentifier();

    String fetchLabelById(Object id);

    Map<Object, String> fetchLabelsById(Set<Object> ids);

    Map<Object, String> fetchLabelsByFilter(String query);

    Slice<Map<Column, Object>> fetchSlice(SliceRequest sliceRequest);
}
