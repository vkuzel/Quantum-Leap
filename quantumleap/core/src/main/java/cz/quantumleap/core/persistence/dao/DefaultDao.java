package cz.quantumleap.core.persistence.dao;

import cz.quantumleap.core.persistence.dao.collection.DefaultOrderBuilder;
import cz.quantumleap.core.persistence.dao.collection.LimitBuilder;
import cz.quantumleap.core.persistence.dao.collection.OrderBuilder;
import cz.quantumleap.core.persistence.dao.lookup.LookupDaoManager;
import cz.quantumleap.core.persistence.transport.Slice;
import cz.quantumleap.core.persistence.transport.SliceRequest;
import org.jooq.*;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class DefaultDao implements CrudDao, CollectionDao, LookupDao {

    protected final Table<Record> table;
    protected final DSLContext dslContext;

    protected final OrderBuilder orderBuilder;
    protected final LimitBuilder limitBuilder;
    protected final MapperFactory mapperFactory;

    protected final CrudDao crudDao;
    protected final CollectionDao collectionDao;
    protected final LookupDao lookupDao;

    protected DefaultDao(Table<Record> table, DSLContext dslContext, LookupDaoManager lookupDaoManager) {
        this.dslContext = dslContext;
        this.table = table;

        this.orderBuilder = new DefaultOrderBuilder(table);
        this.limitBuilder = LimitBuilder.DEFAULT;
        this.mapperFactory = new MapperFactory(table, lookupDaoManager);

        crudDao = new DefaultCrudDao(table, dslContext, mapperFactory);
        collectionDao = new DefaultCollectionDao(table, dslContext, orderBuilder, limitBuilder, mapperFactory);
        lookupDao = new DefaultLookupDao(table, dslContext);
    }

    public <T> Optional<T> fetchById(Object id, Class<T> type) {
        return crudDao.fetchById(id, type);
    }

    public <T> Optional<T> fetchByCondition(Condition condition, Class<T> type) {
        return crudDao.fetchByCondition(condition, type);
    }

    public <T> T save(T object) {
        return crudDao.save(object);
    }

    public void deleteById(Object id) {
        crudDao.deleteById(id);
    }

    public Slice fetchSlice(SliceRequest sliceRequest) {
        return collectionDao.fetchSlice(sliceRequest);
    }

    @Override
    public Table<Record> getTable() {
        return lookupDao.getTable();
    }

    @Override
    public String fetchLabelById(Object id) {
        return lookupDao.fetchLabelById(id);
    }

    @Override
    public Map<Object, String> fetchLabelsById(Set<Object> ids) {
        return lookupDao.fetchLabelsById(ids);
    }
}
