package cz.quantumleap.core.business;

import cz.quantumleap.core.data.LookupDao;

import java.util.Map;

public final class DefaultLookupService implements LookupService {

    private final LookupDao<?> lookupDao;

    public DefaultLookupService(LookupDao<?> lookupDao) {
        this.lookupDao = lookupDao;
    }

    @Override
    public Map<Object, String> findLookupLabels(String filter) {
        return lookupDao.fetchLabelsByFilter(filter);
    }
}
