package cz.quantumleap.core.data;

import cz.quantumleap.core.data.list.FilterBuilder;
import cz.quantumleap.core.data.list.LimitBuilder;
import cz.quantumleap.core.data.list.OrderBuilder;
import cz.quantumleap.core.data.mapper.MapperFactory;
import cz.quantumleap.core.data.mapper.MapperUtils;
import cz.quantumleap.core.data.primarykey.PrimaryKeyResolver;
import cz.quantumleap.core.data.transport.Slice;
import cz.quantumleap.core.data.transport.SliceRequest;
import cz.quantumleap.core.data.transport.TablePreferences;
import org.jooq.*;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static cz.quantumleap.core.data.list.LimitBuilder.Limit;
import static cz.quantumleap.core.tables.TablePreferencesTable.TABLE_PREFERENCES;

public final class DefaultListDao<TABLE extends Table<? extends Record>> implements ListDao<TABLE> {

    private final Table<? extends Record> table;
    private final DSLContext dslContext;

    private final PrimaryKeyResolver primaryKeyResolver;
    private final FilterBuilder filterBuilder;
    private final OrderBuilder orderBuilder;
    private final LimitBuilder limitBuilder;
    private final MapperFactory mapperFactory;

    public DefaultListDao(Table<? extends Record> table, DSLContext dslContext, PrimaryKeyResolver primaryKeyResolver, FilterBuilder filterBuilder, OrderBuilder orderBuilder, LimitBuilder limitBuilder, MapperFactory mapperFactory) {
        this.table = table;
        this.dslContext = dslContext;

        this.primaryKeyResolver = primaryKeyResolver;
        this.filterBuilder = filterBuilder;
        this.orderBuilder = orderBuilder;
        this.limitBuilder = limitBuilder;
        this.mapperFactory = mapperFactory;
    }

    public Slice fetchSlice(SliceRequest sliceRequest) {
        SliceRequest request = setDefaultOrder(sliceRequest);
        Limit limit = limitBuilder.build(sliceRequest);
        Collection<Condition> conditions = joinConditions(
                filterBuilder.buildForFilter(sliceRequest.getFilter()),
                filterBuilder.buildForQuery(sliceRequest.getQuery())
        );

        return dslContext.selectFrom(table)
                .where(conditions)
                .orderBy(orderBuilder.build(request.getSort()))
                .limit(limit.getOffset(), limit.getNumberOfRows())
                .fetchInto(mapperFactory.createSliceMapper(request, fetchTablePreferences())) // TODO Request?
                .intoSlice();
    }

    public <T> List<T> fetchList(SliceRequest sliceRequest, Class<T> type) {
        SliceRequest request = setDefaultOrder(sliceRequest);
        Limit limit = limitBuilder.build(sliceRequest);
        Collection<Condition> conditions = joinConditions(
                filterBuilder.buildForFilter(sliceRequest.getFilter()),
                filterBuilder.buildForQuery(sliceRequest.getQuery())
        );

        return dslContext.selectFrom(table)
                .where(conditions)
                .orderBy(orderBuilder.build(request.getSort()))
                .limit(limit.getOffset(), limit.getNumberOfRows())
                .fetch(mapperFactory.createTransportMapper(type)); // TODO Is this mapper optimized for high volume lists?
    }

    private Collection<Condition> joinConditions(Condition condition1, Condition condition2) {
        List<Condition> conditions = new ArrayList<>(2);
        if (condition1 != null) {
            conditions.add(condition1);
        }
        if (condition2 != null) {
            conditions.add(condition2);
        }
        return conditions;
    }

    private SliceRequest setDefaultOrder(SliceRequest sliceRequest) {
        if (sliceRequest.getSort() == null) {
            List<Field<Object>> primaryKeyFields = primaryKeyResolver.getPrimaryKeyFields();
            List<Sort.Order> orders = primaryKeyFields.stream()
                    .map(field -> new Sort.Order(Sort.Direction.DESC, field.getName()))
                    .collect(Collectors.toList());
            return new SliceRequest(
                    sliceRequest.getFilter(),
                    sliceRequest.getQuery(),
                    sliceRequest.getOffset(),
                    sliceRequest.getSize(),
                    !orders.isEmpty() ? new Sort(orders) : null,
                    sliceRequest.getTablePreferencesId()
            );
        }
        return sliceRequest;
    }

    private List<TablePreferences> fetchTablePreferences() {
        String tableName = MapperUtils.resolveDatabaseTableNameWithSchema(table);
        return dslContext.select(TABLE_PREFERENCES.ID, TABLE_PREFERENCES.IS_DEFAULT, TABLE_PREFERENCES.ENABLED_COLUMNS)
                .from(TABLE_PREFERENCES)
                .where(TABLE_PREFERENCES.DATABASE_TABLE_NAME_WITH_SCHEMA.equal(tableName))
                .fetchInto(TablePreferences.class);
    }
}
