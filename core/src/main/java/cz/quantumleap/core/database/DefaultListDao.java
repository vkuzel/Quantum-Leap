package cz.quantumleap.core.database;

import cz.quantumleap.core.database.domain.FetchParams;
import cz.quantumleap.core.database.domain.TablePreferences;
import cz.quantumleap.core.database.domain.TableSlice;
import cz.quantumleap.core.database.entity.Entity;
import cz.quantumleap.core.database.query.*;
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
    public TableSlice fetchSlice(FetchParams request) {
        request = setDefaultOrder(request);
        Table<?> table = entity.getTable();

        QueryFields queryFields = new QueryFieldsFactory(entity, entityRegistry).createQueryFields();
        FilterFactory filterFactory = new FilterFactory(
                entity.getDefaultCondition(),
                entity.getWordConditionBuilder(),
                queryFields.getQueryFieldMap()
        );
        SortingFactory sortingFactory = new SortingFactory(queryFields.getOrderFieldMap());
        TableSliceFactory tableSliceFactory = new TableSliceFactory(entity);

        Condition condition = filterFactory.forFetchParams(request);
        List<SortField<?>> orderBy = sortingFactory.forFetchParams(request);

        SelectJoinStep<Record> selectJoinStep = dslContext
                .select(queryFields.getQueryFieldMap().values())
                .from(table);
        for (Function<SelectJoinStep<Record>, SelectJoinStep<Record>> joinTable : queryFields.getJoinTables()) {
            selectJoinStep = joinTable.apply(selectJoinStep);
        }
        Result<?> result = selectJoinStep
                .where(condition)
                .orderBy(orderBy)
                .limit(request.getOffset(), resolveNumberOfRows(request))
                .fetch();

        TablePreferences tablePreferences = selectTablePreferences();
        return tableSliceFactory.forRequestedResult(tablePreferences, request, result);
    }

    private int resolveNumberOfRows(FetchParams request) {
        return Math.min(request.getSize() + 1, FetchParams.MAX_ITEMS);
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

    private FetchParams setDefaultOrder(FetchParams fetchParams) {
        if (fetchParams.getSort().isUnsorted()) {
            List<Field<?>> primaryKeyFields = entity.getPrimaryKeyFields();
            List<Sort.Order> orders = primaryKeyFields.stream()
                    .map(field -> Sort.Order.desc(field.getName()))
                    .collect(Collectors.toList());
            return fetchParams.withSort(Sort.by(orders));
        } else {
            return fetchParams;
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
}
