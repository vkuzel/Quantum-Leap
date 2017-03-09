package cz.quantumleap.core.data;

import cz.quantumleap.core.data.collection.DefaultOrderBuilder;
import cz.quantumleap.core.data.collection.LimitBuilder;
import cz.quantumleap.core.data.collection.OrderBuilder;
import cz.quantumleap.core.data.transport.Slice;
import cz.quantumleap.core.data.transport.SliceRequest;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Table;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class DaoStub<TABLE extends Table<? extends Record>> implements DetailDao<TABLE>, ListDao<TABLE>, LookupDao<TABLE> {

    protected final TABLE table;
    protected final DSLContext dslContext;

    protected final OrderBuilder orderBuilder;
    protected final LimitBuilder limitBuilder;
    protected final MapperFactory mapperFactory;

    protected final DetailDao<TABLE> detailDao;
    protected final ListDao<TABLE> listDao;
    protected final LookupDao<TABLE> lookupDao;

    protected DaoStub(TABLE table, DSLContext dslContext, LookupDaoManager lookupDaoManager, RecordAuditor recordAuditor) {
        this.dslContext = dslContext;
        this.table = table;

        this.orderBuilder = new DefaultOrderBuilder(table);
        this.limitBuilder = LimitBuilder.DEFAULT;
        this.mapperFactory = new MapperFactory(table, lookupDaoManager);

        detailDao = new DefaultDetailDao<>(table, dslContext, mapperFactory, recordAuditor);
        listDao = new DefaultListDao<>(table, dslContext, orderBuilder, limitBuilder, mapperFactory);
        lookupDao = new DefaultLookupDao<>(table, dslContext);
    }

    public <T> Optional<T> fetchById(Object id, Class<T> type) {
        return detailDao.fetchById(id, type);
    }

    public <T> Optional<T> fetchByCondition(Condition condition, Class<T> type) {
        return detailDao.fetchByCondition(condition, type);
    }

    public <T> T save(T detail) {
        return detailDao.save(detail);
    }

    public void deleteById(Object id) {
        detailDao.deleteById(id);
    }

    public Slice fetchSlice(SliceRequest sliceRequest) {
        return listDao.fetchSlice(sliceRequest);
    }

    @Override
    public TABLE getTable() {
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

    @Override
    public Map<Object, String> fetchLabelsByFilter(String filter) {
        return lookupDao.fetchLabelsByFilter(filter);
    }
}
