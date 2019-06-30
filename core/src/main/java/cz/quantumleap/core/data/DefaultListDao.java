package cz.quantumleap.core.data;

import cz.quantumleap.core.data.list.FilterBuilder;
import cz.quantumleap.core.data.list.LimitBuilder;
import cz.quantumleap.core.data.list.SortingBuilder;
import cz.quantumleap.core.data.mapper.MapperFactory;
import cz.quantumleap.core.data.mapper.MapperUtils;
import cz.quantumleap.core.data.primarykey.PrimaryKeyResolver;
import cz.quantumleap.core.data.transport.Slice;
import cz.quantumleap.core.data.transport.SliceRequest;
import cz.quantumleap.core.data.transport.Table.Column;
import cz.quantumleap.core.data.transport.TablePreferences;
import org.jooq.*;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cz.quantumleap.core.data.list.LimitBuilder.Limit;
import static cz.quantumleap.core.tables.TablePreferencesTable.TABLE_PREFERENCES;

public final class DefaultListDao<TABLE extends Table<? extends Record>> implements ListDao<TABLE> {

    private final Table<? extends Record> table;
    private final DSLContext dslContext;

    private final PrimaryKeyResolver primaryKeyResolver;
    private final FilterBuilder filterBuilder;
    private final SortingBuilder sortingBuilder;
    private final LimitBuilder limitBuilder;
    private final MapperFactory mapperFactory;

    public DefaultListDao(Table<? extends Record> table, DSLContext dslContext, PrimaryKeyResolver primaryKeyResolver, FilterBuilder filterBuilder, SortingBuilder sortingBuilder, LimitBuilder limitBuilder, MapperFactory mapperFactory) {
        this.table = table;
        this.dslContext = dslContext;

        this.primaryKeyResolver = primaryKeyResolver;
        this.filterBuilder = filterBuilder;
        this.sortingBuilder = sortingBuilder;
        this.limitBuilder = limitBuilder;
        this.mapperFactory = mapperFactory;
    }

    public Slice<Map<Column, Object>> fetchSlice(SliceRequest sliceRequest) {
        SliceRequest request = setDefaultOrder(sliceRequest);
        Limit limit = limitBuilder.build(sliceRequest);
        Collection<Condition> conditions = joinConditions(
                filterBuilder.buildForFilter(sliceRequest.getFilter()),
                filterBuilder.buildForQuery(sliceRequest.getQuery()),
                sliceRequest.getCondition()
        );

        return dslContext.selectFrom(table)
                .where(conditions)
                .orderBy(sortingBuilder.build(request.getSort()))
                .limit(limit.getOffset(), limit.getNumberOfRows())
                .fetchInto(mapperFactory.createSliceMapper(request, fetchTablePreferences())) // TODO Request?
                .intoSlice();
    }

    public <T> List<T> fetchList(SliceRequest sliceRequest, Class<T> type) {
        SliceRequest request = setDefaultOrder(sliceRequest);
        Limit limit = limitBuilder.build(sliceRequest);
        Collection<Condition> conditions = joinConditions(
                filterBuilder.buildForFilter(sliceRequest.getFilter()),
                filterBuilder.buildForQuery(sliceRequest.getQuery()),
                sliceRequest.getCondition()
        );

        return dslContext.selectFrom(table)
                .where(conditions)
                .orderBy(sortingBuilder.build(request.getSort()))
                .limit(limit.getOffset(), limit.getNumberOfRows())
                .fetch(mapperFactory.createTransportMapper(type)); // TODO Is this mapper optimized for high volume lists?
    }

    @Override
    public <T> List<T> fetchListByCondition(Condition condition, Class<T> type) {
        return dslContext.selectFrom(table)
                .where(condition)
                .fetch(mapperFactory.createTransportMapper(type)); // TODO Is this mapper optimized for high volume lists?
    }

    private Collection<Condition> joinConditions(Condition condition1, Condition condition2, Condition condition3) {
        List<Condition> conditions = new ArrayList<>(2);
        if (condition1 != null) {
            conditions.add(condition1);
        }
        if (condition2 != null) {
            conditions.add(condition2);
        }
        if (condition3 != null) {
            conditions.add(condition3);
        }
        return conditions;
    }

    private SliceRequest setDefaultOrder(SliceRequest sliceRequest) {
        if (sliceRequest.getSort().isUnsorted()) {
            List<Field<Object>> primaryKeyFields = primaryKeyResolver.getPrimaryKeyFields();
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
        String tableName = MapperUtils.resolveDatabaseTableNameWithSchema(table);
        return dslContext.select(TABLE_PREFERENCES.ID, TABLE_PREFERENCES.IS_DEFAULT, TABLE_PREFERENCES.ENABLED_COLUMNS)
                .from(TABLE_PREFERENCES)
                .where(TABLE_PREFERENCES.DATABASE_TABLE_NAME_WITH_SCHEMA.equal(tableName))
                .fetchInto(TablePreferences.class);
    }
}
