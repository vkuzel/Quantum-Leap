package cz.quantumleap.core.data;

import cz.quantumleap.core.common.Utils;
import cz.quantumleap.core.data.entity.Entity;
import cz.quantumleap.core.data.entity.EntityIdentifier;
import cz.quantumleap.core.data.query.*;
import cz.quantumleap.core.data.transport.SliceRequest;
import cz.quantumleap.core.data.transport.TablePreferences;
import cz.quantumleap.core.data.transport.TableSlice;
import org.jooq.*;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cz.quantumleap.core.common.Utils.ConditionOperator.AND;
import static cz.quantumleap.core.data.query.LimitFactory.Limit;
import static cz.quantumleap.core.tables.TablePreferencesTable.TABLE_PREFERENCES;

public final class DefaultListDao<TABLE extends Table<? extends Record>> implements ListDao<TABLE> {

    private final Entity<TABLE> entity;
    private final DSLContext dslContext;
    private final EntityManager entityManager;

    public DefaultListDao(Entity<TABLE> entity, DSLContext dslContext, EntityManager entityManager) {
        this.entity = entity;
        this.dslContext = dslContext;
        this.entityManager = entityManager;
    }

    @Override
    public EntityIdentifier<TABLE> getListEntityIdentifier() {
        return entity.getIdentifier();
    }

    public TableSlice fetchSlice(SliceRequest sliceRequest) {
        SliceRequest request = setDefaultOrder(sliceRequest);

        Table<?> table = entity.getTable();
        List<Field<?>> fields = createSliceFieldsFactory().forSliceRequest(request);

        Function<SelectJoinStep<Record>, SelectJoinStep<Record>> join = createSliceJoinFactory().forSliceRequest(sliceRequest);
        Condition conditions = createFilterFactory(fields).forSliceRequest(request);
        List<SortField<?>> orderBy = createSortingFactory(fields).forSliceRequest(request);
        Limit limit = createLimitFactory().forSliceRequest(request);

        SelectJoinStep<Record> selectJoinStep = dslContext
                .select(fields)
                .from(table);
        Result<?> result = join.apply(selectJoinStep)
                .where(conditions)
                .orderBy(orderBy)
                .limit(limit.getOffset(), limit.getNumberOfRows())
                .fetch();

        return createTableSliceFactory().forRequestedResult(request, result);
    }

    public <T> List<T> fetchList(SliceRequest sliceRequest, Class<T> type) {
        SliceRequest request = setDefaultOrder(sliceRequest);

        Table<?> table = entity.getTable();
        List<Field<?>> fields = Arrays.asList(table.fields());

        Condition conditions = createFilterFactory(fields).forSliceRequest(request);
        List<SortField<?>> orderBy = createSortingFactory(fields).forSliceRequest(request);
        Limit limit = createLimitFactory().forSliceRequest(request);

        return dslContext.selectFrom(getTable())
                .where(conditions)
                .orderBy(orderBy)
                .limit(limit.getOffset(), limit.getNumberOfRows())
                .fetchInto(type);
    }

    @Override
    public <T> List<T> fetchListByCondition(Condition condition, Class<T> type) {
        Condition conditions = Utils.joinConditions(
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

    private TableSliceFieldsFactory createSliceFieldsFactory() {
        return new TableSliceFieldsFactory(entity, entityManager);
    }

    private TableSliceJoinFactory createSliceJoinFactory() {
        return new TableSliceJoinFactory(entity, entityManager);
    }

    private FilterFactory createFilterFactory(List<Field<?>> fields) {
        return new FilterFactory(fields, entity.getDefaultFilterCondition(), entity.getWordConditionBuilder());
    }

    private SortingFactory createSortingFactory(List<Field<?>> fields) {
        return new SortingFactory(fields);
    }

    private LimitFactory createLimitFactory() {
        return new LimitFactory();
    }

    private TableSliceFactory createTableSliceFactory() {
        List<TablePreferences> tablePreferences = fetchTablePreferences();
        return new TableSliceFactory(entity, tablePreferences);
    }

    private List<TablePreferences> fetchTablePreferences() {
        return dslContext.select(TABLE_PREFERENCES.ID, TABLE_PREFERENCES.IS_DEFAULT, TABLE_PREFERENCES.ENABLED_COLUMNS)
                .from(TABLE_PREFERENCES)
                .where(TABLE_PREFERENCES.ENTITY_IDENTIFIER.equal(entity.getIdentifier().toString()))
                .fetchInto(TablePreferences.class);
    }
}
