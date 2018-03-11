package cz.quantumleap.core.data;

import cz.quantumleap.core.data.list.*;
import cz.quantumleap.core.data.mapper.MapperFactory;
import cz.quantumleap.core.data.primarykey.PrimaryKeyConditionBuilder;
import cz.quantumleap.core.data.primarykey.PrimaryKeyResolver;
import cz.quantumleap.core.data.primarykey.TablePrimaryKeyResolver;
import cz.quantumleap.core.data.transport.Slice;
import cz.quantumleap.core.data.transport.SliceRequest;
import org.jooq.*;

import java.util.*;
import java.util.function.Function;

public class DaoStub<TABLE extends Table<? extends Record>> implements DetailDao<TABLE>, ListDao<TABLE>, LookupDao<TABLE> {

    protected final TABLE table;
    protected final DSLContext dslContext;

    protected final PrimaryKeyResolver primaryKeyResolver;
    protected final PrimaryKeyConditionBuilder primaryKeyConditionBuilder;
    protected final FilterBuilder filterBuilder;
    protected final OrderBuilder orderBuilder;
    protected final LimitBuilder limitBuilder;
    protected final MapperFactory mapperFactory;

    protected final DetailDao<TABLE> detailDao;
    protected final ListDao<TABLE> listDao;
    protected final LookupDao<TABLE> lookupDao;

    protected DaoStub(TABLE table, Field<String> lookupLabelField, Function<String, Condition> filterConditionBuilder, DSLContext dslContext, LookupDaoManager lookupDaoManager, EnumManager enumManager, RecordAuditor recordAuditor) {
        this.dslContext = dslContext;
        this.table = table;

        this.primaryKeyResolver = new TablePrimaryKeyResolver(table);
        this.primaryKeyConditionBuilder = new PrimaryKeyConditionBuilder(primaryKeyResolver);
        this.filterBuilder = new DefaultFilterBuilder(table);
        this.orderBuilder = new DefaultOrderBuilder(table);
        this.limitBuilder = LimitBuilder.DEFAULT;
        this.mapperFactory = new MapperFactory(table, primaryKeyResolver, lookupDaoManager, enumManager);

        detailDao = new DefaultDetailDao<>(table, dslContext, primaryKeyConditionBuilder, mapperFactory, recordAuditor);
        listDao = new DefaultListDao<>(table, dslContext, primaryKeyResolver, filterBuilder, orderBuilder, limitBuilder, mapperFactory);
        lookupDao = new DefaultLookupDao<>(table, lookupLabelField, dslContext, filterConditionBuilder, primaryKeyConditionBuilder, listDao);
    }

    @Override
    public <T> Optional<T> fetchById(Object id, Class<T> type) {
        return detailDao.fetchById(id, type);
    }

    @Override
    public <T> Optional<T> fetchByCondition(Condition condition, Class<T> type) {
        return detailDao.fetchByCondition(condition, type);
    }

    @Override
    public <T> T save(T detail) {
        return detailDao.save(detail);
    }

    @Override
    public void deleteById(Object id) {
        detailDao.deleteById(id);
    }

    @Override
    public <T> List<T> saveDetailsAssociatedBy(TableField foreignKey, Object foreignId, Collection<T> details, Class<T> detailType) {
        return detailDao.saveDetailsAssociatedBy(foreignKey, foreignId, details, detailType);
    }

    @Override
    public Slice fetchSlice(SliceRequest sliceRequest) {
        return listDao.fetchSlice(sliceRequest);
    }

    @Override
    public <T> List<T> fetchList(SliceRequest sliceRequest, Class<T> type) {
        return listDao.fetchList(sliceRequest, type);
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
