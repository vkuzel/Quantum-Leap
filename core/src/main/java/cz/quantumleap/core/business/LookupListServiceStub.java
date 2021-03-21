package cz.quantumleap.core.business;

import cz.quantumleap.core.database.ListDao;
import cz.quantumleap.core.database.LookupDao;
import cz.quantumleap.core.database.domain.FetchParams;
import cz.quantumleap.core.database.domain.TableSlice;
import cz.quantumleap.core.database.entity.EntityIdentifier;
import org.jooq.Record;
import org.jooq.Table;

import java.util.Map;

public class LookupListServiceStub implements ListService, LookupService {

    protected final ListService listService;
    protected final LookupService lookupService;

    public LookupListServiceStub(ListDao<?> listDao, LookupDao<?> lookupDao) {
        this.listService = new DefaultListService(listDao);
        this.lookupService = new DefaultLookupService(lookupDao, listService);
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
    public String findLookupLabel(Object id) {
        return lookupService.findLookupLabel(id);
    }

    @Override
    public Map<Object, String> findLookupLabels(String query) {
        return lookupService.findLookupLabels(query);
    }

    @Override
    public TableSlice findSlice(FetchParams fetchParams) {
        return listService.findSlice(fetchParams);
    }
}
