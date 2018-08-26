package cz.quantumleap.core.business;

import cz.quantumleap.core.data.ListDao;
import cz.quantumleap.core.data.transport.Slice;
import cz.quantumleap.core.data.transport.SliceRequest;
import cz.quantumleap.core.data.transport.Table;

import java.util.Map;

public final class DefaultListService implements ListService {

    private final ListDao<?> listDao;

    public DefaultListService(ListDao<?> listDao) {
        this.listDao = listDao;
    }

    @Override
    public Slice<Map<Table.Column, Object>> findSlice(SliceRequest sliceRequest) {
        return listDao.fetchSlice(sliceRequest);
    }
}
