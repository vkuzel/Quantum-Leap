package cz.quantumleap.core.business;

import cz.quantumleap.core.common.Utils;
import cz.quantumleap.core.database.ListDao;
import cz.quantumleap.core.database.domain.FetchParams;
import cz.quantumleap.core.database.domain.Slice;
import cz.quantumleap.core.database.entity.EntityIdentifier;
import org.jooq.Record;
import org.jooq.Table;

public final class DefaultListService implements ListService {

    private final ListDao<?> listDao;

    public DefaultListService(ListDao<?> listDao) {
        this.listDao = listDao;
    }

    @Override
    public <TABLE extends Table<? extends Record>> EntityIdentifier<?> getListEntityIdentifier(Class<TABLE> type) {
        var entityIdentifier = listDao.getListEntity().getIdentifier();
        return Utils.checkTableType(entityIdentifier, type);
    }

    @Override
    public Slice findSlice(FetchParams fetchParams) {
        return listDao.fetchSlice(fetchParams);
    }
}
