package cz.quantumleap.core.business;

import cz.quantumleap.core.database.entity.EntityIdentifier;
import cz.quantumleap.core.database.transport.SliceRequest;
import cz.quantumleap.core.database.transport.TableSlice;
import org.jooq.Record;
import org.jooq.Table;

public interface ListService {

    <TABLE extends Table<? extends Record>> EntityIdentifier<?> getListEntityIdentifier(Class<TABLE> type);

    TableSlice findSlice(SliceRequest sliceRequest);
}
