package cz.quantumleap.core.business;

import cz.quantumleap.core.data.transport.Slice;
import cz.quantumleap.core.data.transport.SliceRequest;
import cz.quantumleap.core.data.transport.Table;

import java.util.Map;

public interface ListService {

    Slice<Map<Table.Column, Object>> findSlice(SliceRequest sliceRequest);

}
