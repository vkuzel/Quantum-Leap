package cz.quantumleap.core.data;

import cz.quantumleap.core.data.entity.Entity;
import cz.quantumleap.core.data.entity.EntityIdentifier;
import cz.quantumleap.core.data.transport.SliceRequest;
import cz.quantumleap.core.data.transport.TableSlice;
import org.jooq.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DaoStub<TABLE extends Table<? extends Record>> implements DetailDao<TABLE>, ListDao<TABLE>, LookupDao<TABLE> {

    protected final Entity<TABLE> entity;
    protected final DSLContext dslContext;

    protected final DetailDao<TABLE> detailDao;
    protected final ListDao<TABLE> listDao;
    protected final LookupDao<TABLE> lookupDao;

    protected DaoStub(Entity<TABLE> entity, DSLContext dslContext, RecordAuditor recordAuditor, EntityManager entityManager) {
        this.entity = entity;
        this.dslContext = dslContext;
        this.detailDao = new DefaultDetailDao<>(entity, dslContext, recordAuditor);
        this.listDao = new DefaultListDao<>(entity, dslContext, entityManager);
        this.lookupDao = new DefaultLookupDao<>(entity, dslContext, listDao, entityManager);
    }

    @Override
    public EntityIdentifier<TABLE> getDetailEntityIdentifier() {
        return detailDao.getDetailEntityIdentifier();
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
    public <T> T fetchById(Object id, Class<T> type) {
        return detailDao.fetchById(id, type);
    }

    @Override
    public <T> T fetchByCondition(Condition condition, Class<T> type) {
        return detailDao.fetchByCondition(condition, type);
    }

    @Override
    public <T> T save(T detail) {
        return detailDao.save(detail);
    }

    @Override
    public <T> List<T> saveAll(List<T> details) {
        return detailDao.saveAll(details);
    }

    @Override
    public int deleteById(Object id) {
        return detailDao.deleteById(id);
    }

    @Override
    public int deleteByCondition(Condition condition) {
        return detailDao.deleteByCondition(condition);
    }

    @Override
    public <T, F> List<T> saveDetailsAssociatedBy(TableField<?, F> foreignKey, F foreignId, Collection<T> details, Class<T> detailType) {
        return detailDao.saveDetailsAssociatedBy(foreignKey, foreignId, details, detailType);
    }

    @Override
    public TableSlice fetchSlice(SliceRequest sliceRequest) {
        return listDao.fetchSlice(sliceRequest);
    }

    @Override
    public <T> List<T> fetchList(SliceRequest sliceRequest, Class<T> type) {
        return listDao.fetchList(sliceRequest, type);
    }

    public <T> List<T> fetchListByCondition(Condition condition, Class<T> type) {
        return listDao.fetchListByCondition(condition, type);
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
}
