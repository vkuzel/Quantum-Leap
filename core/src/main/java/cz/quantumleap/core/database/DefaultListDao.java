package cz.quantumleap.core.database;

import cz.quantumleap.core.database.domain.FetchParams;
import cz.quantumleap.core.database.domain.Slice;
import cz.quantumleap.core.database.entity.Entity;
import cz.quantumleap.core.database.query.*;
import cz.quantumleap.core.database.query.SliceQueryFieldsFactory.QueryFields;
import cz.quantumleap.core.slicequery.SliceQueryDao;
import cz.quantumleap.core.slicequery.domain.SliceQuery;
import org.jooq.*;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cz.quantumleap.core.database.query.QueryUtils.ConditionOperator.AND;
import static cz.quantumleap.core.database.query.QueryUtils.joinConditions;

public final class DefaultListDao<TABLE extends Table<? extends Record>> implements ListDao<TABLE> {

    private final Entity<TABLE> entity;
    private final DSLContext dslContext;
    private final EntityRegistry entityRegistry;
    private final SliceQueryDao sliceQueryDao;

    private QueryFields sliceQueryFields;
    private FilterConditionFactory sliceFilterConditionFactory;
    private QueryConditionFactory sliceQueryConditionFactory;
    private SortingFactory sliceSortingFactory;
    private SliceFactory sliceFactory;

    private FilterConditionFactory listFilterConditionFactory;
    private QueryConditionFactory listQueryConditionFactory;
    private SortingFactory listSortingFactory;

    public DefaultListDao(
            Entity<TABLE> entity,
            DSLContext dslContext,
            EntityRegistry entityRegistry,
            SliceQueryDao sliceQueryDao
    ) {
        this.entity = entity;
        this.dslContext = dslContext;
        this.entityRegistry = entityRegistry;
        this.sliceQueryDao = sliceQueryDao;
    }

    @Override
    public Entity<TABLE> getListEntity() {
        return entity;
    }

    @Override
    public Slice fetchSlice(FetchParams params) {
        initFactories();
        List<SliceQuery> sliceQueries = sliceQueryDao.fetchByIdentifierForCurrentUser(entity.getIdentifier());
        params = setParamsDefaultValues(params, sliceQueries);

        Condition condition = QueryUtils.joinConditions(
                AND,
                entity.getCondition(),
                params.getCondition(),
                sliceFilterConditionFactory.forFilter(params.getFilter()),
                sliceQueryConditionFactory.forQuery(params.getQuery())
        );
        List<SortField<?>> orderBy = sliceSortingFactory.forFetchParams(params);

        SelectJoinStep<Record> selectJoinStep = dslContext
                .select(sliceQueryFields.getQueryFieldMap().values())
                .from(entity.getTable());
        for (Function<SelectJoinStep<Record>, SelectJoinStep<Record>> joinTable : sliceQueryFields.getJoinTables()) {
            selectJoinStep = joinTable.apply(selectJoinStep);
        }
        Result<?> result = selectJoinStep
                .where(condition)
                .orderBy(orderBy)
                .limit(params.getOffset(), resolveNumberOfRows(params))
                .fetch();

        return sliceFactory.forRequestedResult(params, result, sliceQueries);
    }

    private FetchParams setParamsDefaultValues(FetchParams fetchParams, List<SliceQuery> sliceQueries) {
        if (fetchParams.getQuery() == null) {
            for (SliceQuery sliceQuery : sliceQueries) {
                if (sliceQuery.isDefault()) {
                    String query = sliceQuery.getQuery();
                    fetchParams = fetchParams.withQuery(query);
                    break;
                }
            }
        }

        if (fetchParams.getSort() == null || fetchParams.getSort().isUnsorted()) {
            List<Field<?>> primaryKeyFields = entity.getPrimaryKeyFields();
            List<Sort.Order> orders = primaryKeyFields.stream()
                    .map(field -> Sort.Order.desc(field.getName()))
                    .collect(Collectors.toList());
            fetchParams = fetchParams.withSort(Sort.by(orders));
        }

        return fetchParams;
    }

    private FetchParams setDefaultOrder(FetchParams fetchParams) {
        if (fetchParams.getSort() == null || fetchParams.getSort().isUnsorted()) {
            List<Field<?>> primaryKeyFields = entity.getPrimaryKeyFields();
            List<Sort.Order> orders = primaryKeyFields.stream()
                    .map(field -> Sort.Order.desc(field.getName()))
                    .collect(Collectors.toList());
            return fetchParams.withSort(Sort.by(orders));
        } else {
            return fetchParams;
        }
    }

    @Override
    public <T> List<T> fetchList(FetchParams params, Class<T> type) {
        initFactories();

        Condition conditions = joinConditions(
                AND,
                entity.getCondition(),
                params.getCondition(),
                listFilterConditionFactory.forFilter(params.getFilter()),
                listQueryConditionFactory.forQuery(params.getQuery())
        );
        List<SortField<?>> orderBy = listSortingFactory.forFetchParams(params);

        return dslContext.selectFrom(entity.getTable())
                .where(conditions)
                .orderBy(orderBy)
                .limit(params.getOffset(), resolveNumberOfRows(params))
                .fetchInto(type);
    }

    private int resolveNumberOfRows(FetchParams request) {
        return Math.min(request.getSize() + 1, FetchParams.MAX_ITEMS);
    }

    /**
     * {@link SliceQueryFieldsFactory} does need {@link EntityRegistry} to
     * be fully initialised to work properly. That is why factories cannot be
     * initialised in the constructor, but has to be lazy-initialised here.
     */
    private void initFactories() {
        if (sliceQueryFields != null) {
            return;
        }

        synchronized (this) {
            if (sliceQueryFields != null) {
                return;
            }

            sliceQueryFields = new SliceQueryFieldsFactory(entity, entityRegistry).createQueryFields();
            sliceFilterConditionFactory = new FilterConditionFactory(sliceQueryFields.getConditionFieldMap());
            sliceQueryConditionFactory = new QueryConditionFactory(entity.getWordConditionBuilder(), sliceQueryFields.getConditionFieldMap());
            sliceSortingFactory = new SortingFactory(sliceQueryFields.getOrderFieldMap());
            sliceFactory = new SliceFactory(entity);

            listFilterConditionFactory = new FilterConditionFactory(entity.getFieldMap());
            listQueryConditionFactory = new QueryConditionFactory(entity.getWordConditionBuilder(), entity.getFieldMap());
            listSortingFactory = new SortingFactory(entity.getFieldMap());
        }
    }
}
