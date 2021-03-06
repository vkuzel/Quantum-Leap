package cz.quantumleap.core.business;

import cz.quantumleap.core.database.DetailDao;
import cz.quantumleap.core.database.ListDao;
import cz.quantumleap.core.database.LookupDao;
import cz.quantumleap.core.database.domain.FetchParams;
import cz.quantumleap.core.database.domain.Slice;
import cz.quantumleap.core.database.entity.EntityIdentifier;
import org.jooq.Record;
import org.jooq.Table;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;

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
    public <TABLE extends Table<? extends Record>> EntityIdentifier<TABLE> getDetailEntityIdentifier(Class<TABLE> type) {
        return detailService.getDetailEntityIdentifier(type);
    }

    @Override
    public <TABLE extends Table<? extends Record>> EntityIdentifier<?> getLookupEntityIdentifier(Class<TABLE> type) {
        return lookupService.getLookupEntityIdentifier(type);
    }

    @Override
    public <TABLE extends Table<? extends Record>> EntityIdentifier<?> getListEntityIdentifier(Class<TABLE> type) {
        return listService.getListEntityIdentifier(type);
    }

    @Override
    public T get(Object id) {
        return detailService.get(id);
    }

    @Transactional
    @Override
    public T save(T detail, Errors errors) {
        return detailService.save(detail, errors);
    }

    @Override
    public Slice findSlice(FetchParams fetchParams) {
        return listService.findSlice(fetchParams);
    }

    @Override
    public String findLookupLabel(Object id) {
        return lookupService.findLookupLabel(id);
    }

    @Override
    public Map<Object, String> findLookupLabels(String query) {
        return lookupService.findLookupLabels(query);
    }
}
