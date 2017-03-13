package cz.quantumleap.core.business;

import cz.quantumleap.core.data.LookupDao;
import cz.quantumleap.core.data.transport.Slice;
import cz.quantumleap.core.data.transport.SliceRequest;

import java.util.Map;

public final class DefaultLookupService implements LookupService {

    private final LookupDao<?> lookupDao;
    private final ListService listService;

    public DefaultLookupService(LookupDao<?> lookupDao, ListService listService) {
        this.lookupDao = lookupDao;
        this.listService = listService;
    }

    @Override
    public Map<Object, String> findLookupLabels(String filter) {
        return lookupDao.fetchLabelsByFilter(filter);
    }

    @Override
    public Slice findSlice(SliceRequest sliceRequest) {
        return listService.findSlice(sliceRequest);
    }
}
