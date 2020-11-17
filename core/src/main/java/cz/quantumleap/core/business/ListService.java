package cz.quantumleap.core.business;

import cz.quantumleap.core.data.entity.EntityIdentifier;
import cz.quantumleap.core.data.transport.Slice;
import cz.quantumleap.core.data.transport.SliceRequest;
import cz.quantumleap.core.data.transport.Table.Column;
import org.jooq.Record;
import org.jooq.Table;

import java.util.Map;

public interface ListService {

    <TABLE extends Table<? extends Record>> EntityIdentifier<?> getListEntityIdentifier(Class<TABLE> type);

    Slice<Map<Column, Object>> findSlice(SliceRequest sliceRequest);
}
