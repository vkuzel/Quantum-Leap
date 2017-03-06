package cz.quantumleap.core.persistence;

import cz.quantumleap.core.persistence.transport.Slice;
import cz.quantumleap.core.persistence.transport.SliceRequest;
import org.jooq.Record;
import org.jooq.Table;

public interface CollectionDao<TABLE extends Table<? extends Record>> {

    Slice fetchSlice(SliceRequest sliceRequest);

}
