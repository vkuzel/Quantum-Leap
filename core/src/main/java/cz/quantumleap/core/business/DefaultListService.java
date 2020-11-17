package cz.quantumleap.core.business;

import cz.quantumleap.core.common.Utils;
import cz.quantumleap.core.data.ListDao;
import cz.quantumleap.core.data.entity.EntityIdentifier;
import cz.quantumleap.core.data.transport.Slice;
import cz.quantumleap.core.data.transport.SliceRequest;
import cz.quantumleap.core.data.transport.Table.Column;
import org.jooq.Record;
import org.jooq.Table;

import java.util.Map;

public final class DefaultListService implements ListService {

    private final ListDao<?> listDao;

    public DefaultListService(ListDao<?> listDao) {
        this.listDao = listDao;
    }

    @Override
    public <TABLE extends Table<? extends Record>> EntityIdentifier<?> getListEntityIdentifier(Class<TABLE> type) {
        return Utils.checkTableType(listDao.getListEntityIdentifier(), type);
    }

    @Override
    public Slice<Map<Column, Object>> findSlice(SliceRequest sliceRequest) {
        return listDao.fetchSlice(sliceRequest);
    }
}
