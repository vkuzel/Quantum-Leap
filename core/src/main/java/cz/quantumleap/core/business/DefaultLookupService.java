package cz.quantumleap.core.business;

import cz.quantumleap.core.common.Utils;
import cz.quantumleap.core.database.LookupDao;
import cz.quantumleap.core.database.domain.FetchParams;
import cz.quantumleap.core.database.domain.Slice;
import cz.quantumleap.core.database.entity.EntityIdentifier;
import org.jooq.Record;
import org.jooq.Table;

import java.util.Map;

public final class DefaultLookupService implements LookupService {

    private final LookupDao<?> lookupDao;
    private final ListService listService;

    public DefaultLookupService(LookupDao<?> lookupDao, ListService listService) {
        this.lookupDao = lookupDao;
        this.listService = listService;
    }

    @Override
    public <TABLE extends Table<? extends Record>> EntityIdentifier<?> getLookupEntityIdentifier(Class<TABLE> type) {
        EntityIdentifier<?> entityIdentifier = lookupDao.getLookupEntity().getIdentifier();
        return Utils.checkTableType(entityIdentifier, type);
    }

    @Override
    public <TABLE extends Table<? extends Record>> EntityIdentifier<?> getListEntityIdentifier(Class<TABLE> type) {
        return listService.getListEntityIdentifier(type);
    }

    @Override
    public String findLookupLabel(Object id) {
        return lookupDao.fetchLabelById(id);
    }

    @Override
    public Map<Object, String> findLookupLabels(String query) {
        return lookupDao.fetchLabelsByFilter(query);
    }

    @Override
    public Slice findSlice(FetchParams fetchParams) {
        return listService.findSlice(fetchParams);
    }
}
