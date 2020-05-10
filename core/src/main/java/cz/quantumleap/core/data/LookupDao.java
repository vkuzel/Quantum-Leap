package cz.quantumleap.core.data;

import cz.quantumleap.core.data.entity.EntityIdentifier;
import cz.quantumleap.core.data.transport.SliceRequest;
import org.jooq.Record;
import org.jooq.Table;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface LookupDao<TABLE extends Table<? extends Record>> extends ListDao<TABLE> {

    EntityIdentifier<TABLE> getEntityIdentifier();

    String fetchLabelById(Object id);

    Map<Object, String> fetchLabelsById(Set<Object> ids);

    Map<Object, String> fetchLabelsByFilter(String query);

    <T> List<T> fetchList(SliceRequest sliceRequest, Class<T> type);
}
