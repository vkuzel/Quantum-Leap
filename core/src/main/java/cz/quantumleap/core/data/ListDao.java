package cz.quantumleap.core.data;

import cz.quantumleap.core.data.transport.Slice;
import cz.quantumleap.core.data.transport.SliceRequest;
import cz.quantumleap.core.data.transport.Table.Column;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Table;

import java.util.List;
import java.util.Map;

public interface ListDao<TABLE extends Table<? extends Record>> {

    Slice<Map<Column, Object>> fetchSlice(SliceRequest sliceRequest);

    <T> List<T> fetchList(SliceRequest sliceRequest, Class<T> type);

    <T> List<T> fetchListByCondition(Condition condition, Class<T> type);
}
