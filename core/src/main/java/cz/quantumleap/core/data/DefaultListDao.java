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

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cz.quantumleap.core.data.list.LimitBuilder.Limit;
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
        List<Field<?>> fields = new TableSliceFieldsFactory(entity, entityManager).forSliceRequest(request);
        Function<SelectJoinStep<Record>, SelectJoinStep<Record>> join = new TableSliceJoinFactory(entity, entityManager).forSliceRequest(request);
        Condition conditions = new TableSliceConditionsFactory(entity, entityManager).forSliceRequest(fields, request); // filter builder - needs fields & entity manager
        Limit limit = new TableSliceLimitFactory(entity, entityManager).forSliceRequest(request);
        List<SortField<?>> orderBy = new TableSliceOrderByFactory(entity, entityManager).forSliceRequest(fields, request); // needs fields

        SelectJoinStep<Record> selectJoinStep = dslContext
                .select(fields)
                .from(table);
        Result<?> result = join.apply(selectJoinStep)
                .where(conditions)
                .orderBy(orderBy)
                .limit(limit.getOffset(), limit.getNumberOfRows())
                .fetch();

        TableSliceFactory tableSliceFactory = new TableSliceFactory(entity, fetchTablePreferences(), request);
        return tableSliceFactory.createTableSlice(result);
    }

    // TODO Similar structure to fetchSlice()
    public <T> List<T> fetchList(SliceRequest sliceRequest, Class<T> type) {
        SliceRequest request = setDefaultOrder(sliceRequest);
        Limit limit = entity.getLimitBuilder().build(sliceRequest);
        Condition conditions = Utils.joinConditions(
                Utils.ConditionOperator.AND,
                entity.getFilterBuilder().buildForFilter(sliceRequest.getFilter()),
                entity.getFilterBuilder().buildForQuery(sliceRequest.getQuery()),
                sliceRequest.getCondition()
        );

        return dslContext.selectFrom(getTable())
                .where(conditions)
                .orderBy(entity.getSortingBuilder().build(request.getSort()))
                .limit(limit.getOffset(), limit.getNumberOfRows())
                .fetchInto(type);
    }

    @Override
    public <T> List<T> fetchListByCondition(Condition condition, Class<T> type) {
        // TODO Default condition...
        return dslContext.selectFrom(getTable())
                .where(condition)
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

    private List<TablePreferences> fetchTablePreferences() {
        return dslContext.select(TABLE_PREFERENCES.ID, TABLE_PREFERENCES.IS_DEFAULT, TABLE_PREFERENCES.ENABLED_COLUMNS)
                .from(TABLE_PREFERENCES)
                .where(TABLE_PREFERENCES.ENTITY_IDENTIFIER.equal(entity.getIdentifier().toString()))
                .fetchInto(TablePreferences.class);
    }

    private Table<? extends Record> getTable() {
        return entity.getTable();
    }
}
