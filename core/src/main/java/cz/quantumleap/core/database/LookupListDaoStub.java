package cz.quantumleap.core.database;

import cz.quantumleap.core.database.domain.FetchParams;
import cz.quantumleap.core.database.domain.TableSlice;
import cz.quantumleap.core.database.entity.Entity;
import org.jooq.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class LookupListDaoStub<TABLE extends Table<? extends Record>> implements LookupDao<TABLE>, ListDao<TABLE> {

    protected final Entity<TABLE> entity;
    protected final DSLContext dslContext;

    protected final ListDao<TABLE> listDao;
    protected final LookupDao<TABLE> lookupDao;

    public LookupListDaoStub(Entity<TABLE> entity, DSLContext dslContext, EntityRegistry entityRegistry) {
        this.entity = entity;
        this.dslContext = dslContext;
        this.listDao = new DefaultListDao<>(entity, dslContext, entityRegistry);
        this.lookupDao = new DefaultLookupDao<>(entity, dslContext, listDao);
    }

    @Override
    public Entity<TABLE> getListEntity() {
        return listDao.getListEntity();
    }

    @Override
    public Entity<?> getLookupEntity() {
        return lookupDao.getLookupEntity();
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
    public TableSlice fetchSlice(FetchParams fetchParams) {
        return listDao.fetchSlice(fetchParams);
    }

    @Override
    public <T> List<T> fetchList(Condition condition, List<SortField<?>> orderBy, int limit, Class<T> type) {
        return listDao.fetchList(condition, orderBy, limit, type);
    }
}
