package cz.quantumleap.core.business;

import cz.quantumleap.core.data.DetailDao;
import cz.quantumleap.core.data.ListDao;
import cz.quantumleap.core.data.LookupDao;
import cz.quantumleap.core.data.transport.Slice;
import cz.quantumleap.core.data.transport.SliceRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

public class ServiceStub<T> implements DetailService<T>, ListService, LookupService {

    protected final Class<T> transportType;
    protected final DetailService<T> detailService;
    protected final ListService listService;
    protected final LookupService lookupService;

    public ServiceStub(Class<T> transportType, DetailDao<?> detailDao, ListDao<?> listDao, LookupDao<?> lookupDao) {
        this.transportType = transportType;
        this.detailService = new DefaultDetailService<>(transportType, detailDao);
        this.listService = new DefaultListService(listDao);
        this.lookupService = new DefaultLookupService(lookupDao, listService);
    }

    @Override
    public T get(Object id) {
        return detailService.get(id);
    }

    @Transactional
    @Override
    public T save(T detail) {
        return detailService.save(detail);
    }

    @Override
    public Slice findSlice(SliceRequest sliceRequest) {
        return listService.findSlice(sliceRequest);
    }

    @Override
    public String findLookupLabel(Object id) {
        return lookupService.findLookupLabel(id);
    }

    @Override
    public Map<Object, String> findLookupLabels(String filter) {
        return lookupService.findLookupLabels(filter);
    }
}
