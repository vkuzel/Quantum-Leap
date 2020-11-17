package cz.quantumleap.core.data;

import cz.quantumleap.core.common.Utils;
import cz.quantumleap.core.data.entity.Entity;
import cz.quantumleap.core.data.entity.EntityIdentifier;
import cz.quantumleap.core.data.mapper.MapperFactory;
import cz.quantumleap.core.data.transport.Slice;
import cz.quantumleap.core.data.transport.SliceRequest;
import cz.quantumleap.core.data.transport.Table.Column;
import cz.quantumleap.core.data.transport.TablePreferences;
import org.jooq.*;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cz.quantumleap.core.data.list.LimitBuilder.Limit;
import static cz.quantumleap.core.tables.TablePreferencesTable.TABLE_PREFERENCES;

public final class DefaultListDao<TABLE extends Table<? extends Record>> implements ListDao<TABLE> {

    private final Entity<TABLE> entity;
    private final DSLContext dslContext;
    private final MapperFactory<TABLE> mapperFactory;

    public DefaultListDao(Entity<TABLE> entity, DSLContext dslContext, MapperFactory<TABLE> mapperFactory) {
        this.entity = entity;
        this.dslContext = dslContext;
        this.mapperFactory = mapperFactory;
    }

    @Override
    public EntityIdentifier<TABLE> getListEntityIdentifier() {
        return entity.getIdentifier();
    }

    public Slice<Map<Column, Object>> fetchSlice(SliceRequest sliceRequest) {
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
                .fetchInto(mapperFactory.createSliceMapper(request, fetchTablePreferences())) // TODO Request?
                .intoSlice();
    }

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
                .fetch(mapperFactory.createTransportMapper(type)); // TODO Is this mapper optimized for high volume lists?
    }

    @Override
    public <T> List<T> fetchListByCondition(Condition condition, Class<T> type) {
        // TODO Default condition...
        return dslContext.selectFrom(getTable())
                .where(condition)
                .fetch(mapperFactory.createTransportMapper(type)); // TODO Is this mapper optimized for high volume lists?
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
