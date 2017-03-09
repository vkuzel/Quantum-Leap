package cz.quantumleap.core.business;

import cz.quantumleap.core.data.ListDao;
import cz.quantumleap.core.data.transport.Slice;
import cz.quantumleap.core.data.transport.SliceRequest;

public final class DefaultListService implements ListService {

    private final ListDao<?> listDao;

    public DefaultListService(ListDao<?> listDao) {
        this.listDao = listDao;
    }

    @Override
    public Slice findSlice(SliceRequest sliceRequest) {
        return listDao.fetchSlice(sliceRequest);
    }
}
