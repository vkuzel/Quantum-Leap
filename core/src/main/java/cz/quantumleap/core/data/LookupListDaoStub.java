package cz.quantumleap.core.data;

import cz.quantumleap.core.data.entity.Entity;
import cz.quantumleap.core.data.entity.EntityIdentifier;
import cz.quantumleap.core.data.transport.SliceRequest;
import cz.quantumleap.core.data.transport.TableSlice;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Table;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class LookupListDaoStub<TABLE extends Table<? extends Record>> implements LookupDao<TABLE>, ListDao<TABLE> {

    protected final Entity<TABLE> entity;
    protected final DSLContext dslContext;

    protected final ListDao<TABLE> listDao;
    protected final LookupDao<TABLE> lookupDao;

    public LookupListDaoStub(Entity<TABLE> entity, DSLContext dslContext, EntityManager entityManager) {
        this.entity = entity;
        this.dslContext = dslContext;
        this.listDao = new DefaultListDao<>(entity, dslContext, entityManager);
        this.lookupDao = new DefaultLookupDao<>(entity, dslContext, listDao, entityManager);
    }

    @Override
    public EntityIdentifier<TABLE> getListEntityIdentifier() {
        return listDao.getListEntityIdentifier();
    }

    @Override
    public EntityIdentifier<?> getLookupEntityIdentifier() {
        return lookupDao.getLookupEntityIdentifier();
    }

    @Override
    public String fetchLabelById(Object id) {
        return lookupDao.fetchLabelById(id);
    }

    @Override
    public Map<Object, String> fetchLabelsById(Set<Object> ids) {
        return lookupDao.fetchLabelsById(ids);
    }

    @Override
    public Map<Object, String> fetchLabelsByFilter(String query) {
        return lookupDao.fetchLabelsByFilter(query);
    }


    @Override
    public TableSlice fetchSlice(SliceRequest sliceRequest) {
        return listDao.fetchSlice(sliceRequest);
    }

    @Override
    public <T> List<T> fetchList(SliceRequest sliceRequest, Class<T> type) {
        return listDao.fetchList(sliceRequest, type);
    }

    @Override
    public <T> List<T> fetchListByCondition(Condition condition, Class<T> type) {
        return listDao.fetchListByCondition(condition, type);
    }
}
