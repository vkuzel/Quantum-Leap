package cz.quantumleap.core.database;

import cz.quantumleap.core.database.domain.SliceRequest;
import cz.quantumleap.core.database.domain.TableSlice;
import cz.quantumleap.core.database.entity.Entity;
import cz.quantumleap.core.database.query.FilterFactory;
import org.apache.commons.lang3.StringUtils;
import org.jooq.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public final class DefaultLookupDao<TABLE extends Table<? extends Record>> implements LookupDao<TABLE> {

    private static final int MAX_FILTERED_ROWS = 10;

    private final Entity<TABLE> entity;
    private final DSLContext dslContext;
    private final ListDao<TABLE> listDao;

    public DefaultLookupDao(Entity<TABLE> entity, DSLContext dslContext, ListDao<TABLE> listDao) {
        this.entity = entity;
        this.dslContext = dslContext;
        this.listDao = listDao;
    }

    @Override
    public Entity<?> getLookupEntity() {
        return entity;
    }

    public String fetchLabelById(Object id) {
        Condition condition = entity.getPrimaryKeyConditionBuilder().buildFromId(id);

        return dslContext.select(entity.getLookupLabelField())
                .from(getTable())
                .where(condition)
                .orderBy(createSortField())
                .fetchOneInto(String.class);
    }

    public Map<Object, String> fetchLabelsById(Set<Object> ids) {
        Field<?> primaryKey = entity.getPrimaryKeyField();
        Condition condition = entity.getPrimaryKeyConditionBuilder().buildFromIds(ids);

        return dslContext.select(primaryKey, entity.getLookupLabelField())
                .from(getTable())
                .where(condition)
                .orderBy(createSortField())
                .fetchMap(Record2::value1, Record2::value2);
    }

    @Override
    public Map<Object, String> fetchLabelsByFilter(String query) {
        if (StringUtils.isEmpty(query)) {
            return Collections.emptyMap();
        }

        Field<?> primaryKey = entity.getPrimaryKeyField();
        Condition condition = createFilterFactory().forQuery(query);

        return dslContext.select(primaryKey, entity.getLookupLabelField())
                .from(getTable())
                .where(condition)
                .orderBy(createSortField())
                .limit(MAX_FILTERED_ROWS)
                .fetchMap(Record2::value1, Record2::value2);
    }

    @Override
    public TableSlice fetchTableSlice(SliceRequest sliceRequest) {
        return listDao.fetchTableSlice(sliceRequest);
    }

    private SortField<?> createSortField() {
        Field<?> field = entity.getLookupLabelField();
        return field.asc();
    }

    private Table<? extends Record> getTable() {
        return entity.getTable();
    }

    private FilterFactory createFilterFactory() {
        return new FilterFactory(
                Arrays.asList(entity.getTable().fields()),
                entity.getDefaultFilterCondition(),
                entity.getWordConditionBuilder()
        );
    }
}