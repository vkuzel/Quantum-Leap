package cz.quantumleap.core.data;

import cz.quantumleap.core.data.entity.EntityIdentifier;
import cz.quantumleap.core.data.transport.SliceRequest;
import cz.quantumleap.core.data.transport.TableSlice;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Table;

import java.util.List;

public interface ListDao<TABLE extends Table<? extends Record>> {

    EntityIdentifier<TABLE> getListEntityIdentifier();

    TableSlice fetchSlice(SliceRequest sliceRequest);

    <T> List<T> fetchList(SliceRequest sliceRequest, Class<T> type);

    <T> List<T> fetchListByCondition(Condition condition, Class<T> type);
}
