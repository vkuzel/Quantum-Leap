package cz.quantumleap.core.database;

import cz.quantumleap.core.database.domain.SliceRequest;
import cz.quantumleap.core.database.domain.TablePreferences;
import cz.quantumleap.core.database.domain.TableSlice;
import cz.quantumleap.core.database.entity.Entity;
import cz.quantumleap.core.database.query.*;
import cz.quantumleap.core.database.query.LimitFactory.Limit;
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
    private final QueryFieldsFactory queryFieldsFactory;
    private final FilterFactory filterFactory;
    private final SortingFactory sortingFactory;
    private final LimitFactory limitFactory;
    private final TableSliceFactory tableSliceFactory;

    private DefaultListDao(
            Entity<TABLE> entity,
            DSLContext dslContext,
            QueryFieldsFactory queryFieldsFactory,
            FilterFactory filterFactory,
            SortingFactory sortingFactory,
            LimitFactory limitFactory,
            TableSliceFactory tableSliceFactory
    ) {
        this.entity = entity;
        this.dslContext = dslContext;
        this.queryFieldsFactory = queryFieldsFactory;
        this.filterFactory = filterFactory;
        this.sortingFactory = sortingFactory;
        this.limitFactory = limitFactory;
        this.tableSliceFactory = tableSliceFactory;
    }

    public static <TABLE extends Table<? extends Record>> Builder<TABLE> builder(
            Entity<TABLE> entity,
            DSLContext dslContext,
            EntityRegistry entityRegistry
    ) {
        return new Builder<>(entity, dslContext, entityRegistry);
    }

    @Override
    public Entity<TABLE> getListEntity() {
        return entity;
    }

    @Override
    public TableSlice fetchSlice(SliceRequest request) {
        request = setDefaultOrder(request);
        Table<?> table = entity.getTable();
        QueryFields queryFields = queryFieldsFactory.createQueryFields();
        Condition condition = filterFactory.forSliceRequest(queryFields.getFilterFieldMap(), request);
        List<SortField<?>> orderBy = sortingFactory.forSliceRequest(queryFields.getOrderFieldMap(), request);
        Limit limit = limitFactory.forSliceRequest(request);

        SelectJoinStep<Record> selectJoinStep = dslContext
                .select(queryFields.getQueryFieldMap().values())
                .from(table);
        for (Function<SelectJoinStep<Record>, SelectJoinStep<Record>> joinTable : queryFields.getJoinTables()) {
            selectJoinStep = joinTable.apply(selectJoinStep);
        }
        Result<?> result = selectJoinStep
                .where(condition)
                .orderBy(orderBy)
                .limit(limit.getOffset(), limit.getNumberOfRows())
                .fetch();

        TablePreferences tablePreferences = selectTablePreferences();
        return tableSliceFactory.forRequestedResult(tablePreferences, request, result);
    }

    public <T> List<T> fetchList(Condition condition, List<SortField<?>> orderBy, int limit, Class<T> type) {
        Condition conditions = joinConditions(
                AND,
                entity.getDefaultCondition(),
                condition
        );
        return dslContext.selectFrom(getTable())
                .where(conditions)
                .orderBy(orderBy)
                .limit(limit)
                .fetchInto(type);
    }

    private SliceRequest setDefaultOrder(SliceRequest sliceRequest) {
        if (sliceRequest.getSort().isUnsorted()) {
            List<Field<?>> primaryKeyFields = entity.getPrimaryKeyFields();
            List<Sort.Order> orders = primaryKeyFields.stream()
                    .map(field -> Sort.Order.desc(field.getName()))
                    .collect(Collectors.toList());
            return sliceRequest.withSort(Sort.by(orders));
        } else {
            return sliceRequest;
        }
    }

    private Table<? extends Record> getTable() {
        return entity.getTable();
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

    public static class Builder<TABLE extends Table<? extends Record>> {

        private final Entity<TABLE> entity;
        private final DSLContext dslContext;
        private final EntityRegistry entityRegistry;

        private Builder(Entity<TABLE> entity, DSLContext dslContext, EntityRegistry entityRegistry) {
            this.entity = entity;
            this.dslContext = dslContext;
            this.entityRegistry = entityRegistry;
        }

        public DefaultListDao<TABLE> build() {
            QueryFieldsFactory queryFieldsFactory = new QueryFieldsFactory(entity, entityRegistry);
            FilterFactory filterFactory = new DefaultFilterFactory(entity.getDefaultCondition(), entity.getWordConditionBuilder());
            SortingFactory sortingFactory = new DefaultSortingFactory(entity.getLookupLabelField());
            LimitFactory limitFactory = new DefaultLimitFactory();
            TableSliceFactory tableSliceFactory = new DefaultTableSliceFactory(entity);

            return new DefaultListDao<>(
                    entity,
                    dslContext,
                    queryFieldsFactory,
                    filterFactory,
                    sortingFactory,
                    limitFactory,
                    tableSliceFactory
            );
        }
    }
}
