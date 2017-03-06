package cz.quantumleap.core.persistence.dao;

import cz.quantumleap.core.persistence.transport.Slice;
import cz.quantumleap.core.persistence.transport.SliceRequest;

public interface CollectionDao {

    Slice fetchSlice(SliceRequest sliceRequest);

}
