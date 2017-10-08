package cz.quantumleap.core.business;

import cz.quantumleap.core.data.transport.Slice;
import cz.quantumleap.core.data.transport.SliceRequest;

public interface ListService {

    Slice findSlice(SliceRequest sliceRequest);

}
