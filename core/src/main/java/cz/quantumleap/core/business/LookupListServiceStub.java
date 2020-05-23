package cz.quantumleap.core.business;

import cz.quantumleap.core.data.ListDao;
import cz.quantumleap.core.data.LookupDao;
import cz.quantumleap.core.data.entity.EntityIdentifier;
import cz.quantumleap.core.data.transport.Slice;
import cz.quantumleap.core.data.transport.SliceRequest;
import cz.quantumleap.core.data.transport.Table;

import java.util.Map;

public class LookupListServiceStub implements ListService, LookupService {

    protected final ListService listService;
    protected final LookupService lookupService;

    public LookupListServiceStub(ListDao<?> listDao, LookupDao<?> lookupDao) {
        this.listService = new DefaultListService(listDao);
        this.lookupService = new DefaultLookupService(lookupDao, listService);
    }

    @Override
    public EntityIdentifier<?> getLookupEntityIdentifier() {
        return lookupService.getLookupEntityIdentifier();
    }

    @Override
    public EntityIdentifier<?> getListEntityIdentifier() {
        return listService.getListEntityIdentifier();
    }

    @Override
    public String findLookupLabel(Object id) {
        return lookupService.findLookupLabel(id);
    }

    @Override
    public Map<Object, String> findLookupLabels(String query) {
        return lookupService.findLookupLabels(query);
    }

    @Override
    public Slice<Map<Table.Column, Object>> findSlice(SliceRequest sliceRequest) {
        return listService.findSlice(sliceRequest);
    }
}
