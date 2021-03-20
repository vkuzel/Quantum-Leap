package cz.quantumleap.core.database;

import cz.quantumleap.core.database.domain.SliceRequest;
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

    private final QueryFields queryFields;
    private final FilterFactory filterFactory;
    private final SortingFactory sortingFactory;
    private final TableSliceFactory tableSliceFactory;

    private DefaultListDao(
            Entity<TABLE> entity,
            DSLContext dslContext,
            EntityRegistry entityRegistry
    ) {
        this.entity = entity;
        this.dslContext = dslContext;

        this.queryFields = new QueryFieldsFactory(entity, entityRegistry).createQueryFields();
        this.filterFactory = new FilterFactory(
                entity.getDefaultCondition(),
                entity.getWordConditionBuilder(),
                queryFields.getQueryFieldMap()
        );
        this.sortingFactory = new SortingFactory(queryFields.getOrderFieldMap());
        this.tableSliceFactory = new TableSliceFactory(entity);
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
        Condition condition = filterFactory.forSliceRequest(request);
        List<SortField<?>> orderBy = sortingFactory.forSliceRequest(request);

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

    private int resolveNumberOfRows(SliceRequest request) {
        return Math.min(request.getSize() + 1, SliceRequest.MAX_ITEMS);
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
            return new DefaultListDao<>(
                    entity,
                    dslContext,
                    entityRegistry
            );
        }
    }
}
