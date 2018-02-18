package cz.quantumleap.core.data;

import cz.quantumleap.core.data.transport.Slice;
import cz.quantumleap.core.data.transport.SliceRequest;
import org.jooq.Record;
import org.jooq.Table;

import java.util.List;

public interface ListDao<TABLE extends Table<? extends Record>> {

    Slice fetchSlice(SliceRequest sliceRequest);

    <T> List<T> fetchList(SliceRequest sliceRequest, Class<T> type);
}
