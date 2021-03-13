package cz.quantumleap.core.database;

import cz.quantumleap.core.database.domain.SliceRequest;
import cz.quantumleap.core.database.domain.TablePreferences;
import cz.quantumleap.core.database.domain.TableSlice;
import cz.quantumleap.core.database.entity.Entity;
import cz.quantumleap.core.database.query.*;
import org.jooq.*;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cz.quantumleap.core.database.query.LimitFactory.Limit;
import static cz.quantumleap.core.database.query.QueryUtils.ConditionOperator.AND;
import static cz.quantumleap.core.database.query.QueryUtils.joinConditions;
import static cz.quantumleap.core.tables.TablePreferencesTable.TABLE_PREFERENCES;

public final class DefaultListDao<TABLE extends Table<? extends Record>> implements ListDao<TABLE> {

    private final Entity<TABLE> entity;
    private final DSLContext dslContext;
    private final TableSliceFieldsFactory tableSliceFieldsFactory;
    private final TableSliceJoinFactory tableSliceJoinFactory;
    private final FilterFactory filterFactory;
    private final SortingFactory sortingFactory;
    private final LimitFactory limitFactory;
    private final TableSliceFactory tableSliceFactory;

    private DefaultListDao(
            Entity<TABLE> entity,
            DSLContext dslContext,
            TableSliceFieldsFactory tableSliceFieldsFactory,
            TableSliceJoinFactory tableSliceJoinFactory,
            FilterFactory filterFactory,
            SortingFactory sortingFactory,
            LimitFactory limitFactory,
            TableSliceFactory tableSliceFactory
    ) {
        this.entity = entity;
        this.dslContext = dslContext;
        this.tableSliceFieldsFactory = tableSliceFieldsFactory;
        this.tableSliceJoinFactory = tableSliceJoinFactory;
        this.filterFactory = filterFactory;
        this.sortingFactory = sortingFactory;
        this.limitFactory = limitFactory;
        this.tableSliceFactory = tableSliceFactory;
    }

    public static <TABLE extends Table<? extends Record>> Builder<TABLE> createBuilder(
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

    public TableSlice fetchSlice(SliceRequest sliceRequest) {
        SliceRequest request = setDefaultOrder(sliceRequest);

        Table<?> table = entity.getTable();
        List<Field<?>> fields = tableSliceFieldsFactory.forSliceRequest(request);

        Function<SelectJoinStep<Record>, SelectJoinStep<Record>> join = tableSliceJoinFactory.forSliceRequest(sliceRequest);
        Condition conditions = filterFactory.forSliceRequest(fields, request);
        List<SortField<?>> orderBy = sortingFactory.forSliceRequest(fields, request);
        Limit limit = limitFactory.forSliceRequest(request);

        SelectJoinStep<Record> selectJoinStep = dslContext
                .select(fields)
                .from(table);
        Result<?> result = join.apply(selectJoinStep)
                .where(conditions)
                .orderBy(orderBy)
                .limit(limit.getOffset(), limit.getNumberOfRows())
                .fetch();

        TablePreferences tablePreferences = selectTablePreferences();
        return tableSliceFactory.forRequestedResult(tablePreferences, request, result);
    }

    public <T> List<T> fetchList(SliceRequest sliceRequest, Class<T> type) {
        SliceRequest request = setDefaultOrder(sliceRequest);

        Table<?> table = entity.getTable();
        List<Field<?>> fields = Arrays.asList(table.fields());

        Condition conditions = filterFactory.forSliceRequest(fields, request);
        List<SortField<?>> orderBy = sortingFactory.forSliceRequest(fields, request);
        Limit limit = limitFactory.forSliceRequest(request);

        return dslContext.selectFrom(getTable())
                .where(conditions)
                .orderBy(orderBy)
                .limit(limit.getOffset(), limit.getNumberOfRows())
                .fetchInto(type);
    }

    @Override
    public <T> List<T> fetchListByCondition(Condition condition, Class<T> type) {
        Condition conditions = joinConditions(
                AND,
                entity.getDefaultFilterCondition(),
                condition
        );
        return dslContext.selectFrom(getTable())
                .where(conditions)
                .fetchInto(type);
    }

    private SliceRequest setDefaultOrder(SliceRequest sliceRequest) {
        if (sliceRequest.getSort().isUnsorted()) {
            List<Field<?>> primaryKeyFields = entity.getPrimaryKeyFields();
            List<Sort.Order> orders = primaryKeyFields.stream()
                    .map(field -> Sort.Order.desc(field.getName()))
                    .collect(Collectors.toList());
            return new SliceRequest(
                    sliceRequest.getFilter(),
                    sliceRequest.getQuery(),
                    sliceRequest.getCondition(),
                    sliceRequest.getOffset(),
                    sliceRequest.getSize(),
                    Sort.by(orders),
                    sliceRequest.getTablePreferencesId()
            );
        }
        return sliceRequest;
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
        private TableSliceFieldsFactory tableSliceFieldsFactory = null;
        private TableSliceJoinFactory tableSliceJoinFactory = null;
        private FilterFactory filterFactory = null;
        private SortingFactory sortingFactory = null;
        private LimitFactory limitFactory = null;
        private TableSliceFactory tableSliceFactory = null;

        private Builder(Entity<TABLE> entity, DSLContext dslContext, EntityRegistry entityRegistry) {
            this.entity = entity;
            this.dslContext = dslContext;
            this.entityRegistry = entityRegistry;
        }

        @SuppressWarnings("unused")
        public Builder<TABLE> setTableSliceFieldsFactory(TableSliceFieldsFactory tableSliceFieldsFactory) {
            this.tableSliceFieldsFactory = tableSliceFieldsFactory;
            return this;
        }

        @SuppressWarnings("unused")
        public Builder<TABLE> setTableSliceJoinFactory(TableSliceJoinFactory tableSliceJoinFactory) {
            this.tableSliceJoinFactory = tableSliceJoinFactory;
            return this;
        }

        @SuppressWarnings("unused")
        public Builder<TABLE> setFilterFactory(FilterFactory filterFactory) {
            this.filterFactory = filterFactory;
            return this;
        }

        @SuppressWarnings("unused")
        public Builder<TABLE> setSortingFactory(SortingFactory sortingFactory) {
            this.sortingFactory = sortingFactory;
            return this;
        }

        @SuppressWarnings("unused")
        public Builder<TABLE> setLimitFactory(LimitFactory limitFactory) {
            this.limitFactory = limitFactory;
            return this;
        }

        @SuppressWarnings("unused")
        public Builder<TABLE> setTableSliceFactory(TableSliceFactory tableSliceFactory) {
            this.tableSliceFactory = tableSliceFactory;
            return this;
        }

        public DefaultListDao<TABLE> build() {
            TableSliceFieldsFactory tableSliceFieldsFactory = this.tableSliceFieldsFactory;
            if (tableSliceFieldsFactory == null) {
                tableSliceFieldsFactory = new DefaultTableSliceFieldsFactory(entity, entityRegistry);
            }
            TableSliceJoinFactory tableSliceJoinFactory = this.tableSliceJoinFactory;
            if (tableSliceJoinFactory == null) {
                tableSliceJoinFactory = new DefaultTableSliceJoinFactory(entity, entityRegistry);
            }
            FilterFactory filterFactory = this.filterFactory;
            if (filterFactory == null) {
                filterFactory = new DefaultFilterFactory(entity.getDefaultFilterCondition(), entity.getWordConditionBuilder());
            }
            SortingFactory sortingFactory = this.sortingFactory;
            if (sortingFactory == null) {
                sortingFactory = new DefaultSortingFactory(entity.getLookupLabelField());
            }
            LimitFactory limitFactory = this.limitFactory;
            if (limitFactory == null) {
                limitFactory = new DefaultLimitFactory();
            }
            TableSliceFactory tableSliceFactory = this.tableSliceFactory;
            if (tableSliceFactory == null) {
                tableSliceFactory = new DefaultTableSliceFactory(entity);
            }

            return new DefaultListDao<>(
                    entity,
                    dslContext,
                    tableSliceFieldsFactory,
                    tableSliceJoinFactory,
                    filterFactory,
                    sortingFactory,
                    limitFactory,
                    tableSliceFactory
            );
        }
    }
}
