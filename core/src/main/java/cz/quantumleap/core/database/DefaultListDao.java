package cz.quantumleap.core.database;

import cz.quantumleap.core.database.domain.FetchParams;
import cz.quantumleap.core.database.domain.TablePreferences;
import cz.quantumleap.core.database.domain.TableSlice;
import cz.quantumleap.core.database.entity.Entity;
import cz.quantumleap.core.database.query.*;
import cz.quantumleap.core.database.query.TableSliceQueryFieldsFactory.QueryFields;
import org.jooq.*;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cz.quantumleap.core.database.query.QueryUtils.ConditionOperator.AND;
import static cz.quantumleap.core.database.query.QueryUtils.joinConditions;
import static cz.quantumleap.core.tables.TablePreferencesTable.TABLE_PREFERENCES;

public final class DefaultListDao<TABLE extends Table<? extends Record>> implements ListDao<TABLE> {

    private final Entity<TABLE> entity;
    private final DSLContext dslContext;
    private final EntityRegistry entityRegistry;

    private QueryFields sliceQueryFields;
    private FilterConditionFactory sliceFilterConditionFactory;
    private QueryConditionFactory sliceQueryConditionFactory;
    private SortingFactory sliceSortingFactory;
    private TableSliceFactory sliceFactory;

    private FilterConditionFactory listFilterConditionFactory;
    private QueryConditionFactory listQueryConditionFactory;
    private SortingFactory listSortingFactory;

    public DefaultListDao(Entity<TABLE> entity, DSLContext dslContext, EntityRegistry entityRegistry) {
        this.entity = entity;
        this.dslContext = dslContext;
        this.entityRegistry = entityRegistry;
    }

    @Override
    public Entity<TABLE> getListEntity() {
        return entity;
    }

    @Override
    public TableSlice fetchSlice(FetchParams params) {
        initFactories();
        params = setDefaultOrder(params);

        Condition condition = QueryUtils.joinConditions(
                AND,
                entity.getDefaultCondition(),
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

        TablePreferences tablePreferences = selectTablePreferences();
        return sliceFactory.forRequestedResult(tablePreferences, params, result);
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
                entity.getDefaultCondition(),
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

    private TablePreferences selectTablePreferences() {
        List<TablePreferences> tablePreferencesList = dslContext.select(
                TABLE_PREFERENCES.ID,
                TABLE_PREFERENCES.IS_DEFAULT,
                TABLE_PREFERENCES.ENABLED_COLUMNS
        )
                .from(TABLE_PREFERENCES)
                .where(TABLE_PREFERENCES.ENTITY_IDENTIFIER.equal(entity.getIdentifier().toString()))
                .fetchInto(TablePreferences.class);

        for (TablePreferences preferences : tablePreferencesList) {
            if (preferences.isDefault()) {
                return preferences;
            }
        }
        return TablePreferences.EMPTY;
    }

    /**
     * {@link TableSliceQueryFieldsFactory} does need {@link EntityRegistry} to
     * be fully initialised to work properly. That is why factories cannot be
     * initialised in the constructor, but has to be lazy-initialised here.
     */
    private synchronized void initFactories() {
        if (sliceQueryFields != null) {
            return;
        }

        sliceQueryFields = new TableSliceQueryFieldsFactory(entity, entityRegistry).createQueryFields();
        sliceFilterConditionFactory = new FilterConditionFactory(sliceQueryFields.getConditionFieldMap());
        sliceQueryConditionFactory = new QueryConditionFactory(entity.getWordConditionBuilder(), sliceQueryFields.getConditionFieldMap());
        sliceSortingFactory = new SortingFactory(sliceQueryFields.getOrderFieldMap());
        sliceFactory = new TableSliceFactory(entity);

        listFilterConditionFactory = new FilterConditionFactory(entity.getFieldMap());
        listQueryConditionFactory = new QueryConditionFactory(entity.getWordConditionBuilder(), entity.getFieldMap());
        listSortingFactory = new SortingFactory(entity.getFieldMap());
    }
}
