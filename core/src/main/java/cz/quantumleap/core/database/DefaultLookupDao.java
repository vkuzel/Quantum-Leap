package cz.quantumleap.core.database;

import cz.quantumleap.core.database.domain.SliceRequest;
import cz.quantumleap.core.database.domain.TableSlice;
import cz.quantumleap.core.database.entity.Entity;
import cz.quantumleap.core.database.query.DefaultFilterFactory;
import cz.quantumleap.core.database.query.DefaultSortingFactory;
import cz.quantumleap.core.database.query.FilterFactory;
import cz.quantumleap.core.database.query.SortingFactory;
import org.apache.commons.lang3.StringUtils;
import org.jooq.*;

import java.util.*;

public final class DefaultLookupDao<TABLE extends Table<? extends Record>> implements LookupDao<TABLE> {

    private static final int MAX_FILTERED_ROWS = 10;

    private final Entity<TABLE> entity;
    private final DSLContext dslContext;
    private final ListDao<TABLE> listDao;
    private final FilterFactory filterFactory;
    private final SortingFactory sortingFactory;

    private DefaultLookupDao(
            Entity<TABLE> entity,
            DSLContext dslContext,
            ListDao<TABLE> listDao,
            FilterFactory filterFactory,
            SortingFactory sortingFactory
    ) {
        this.entity = entity;
        this.dslContext = dslContext;
        this.listDao = listDao;
        this.filterFactory = filterFactory;
        this.sortingFactory = sortingFactory;
    }

    public static <TABLE extends Table<? extends Record>> Builder<TABLE> createBuilder(
            Entity<TABLE> entity,
            DSLContext dslContext,
            ListDao<TABLE> listDao
    ) {
        return new Builder<>(entity, dslContext, listDao);
    }

    @Override
    public Entity<?> getLookupEntity() {
        return entity;
    }

    public String fetchLabelById(Object id) {
        Condition condition = entity.getPrimaryKeyConditionBuilder().buildFromId(id);
        List<SortField<?>> sortFields = sortingFactory.forLookup();

        return dslContext.select(entity.getLookupLabelField())
                .from(getTable())
                .where(condition)
                .orderBy(sortFields)
                .fetchOneInto(String.class);
    }

    public Map<Object, String> fetchLabelsById(Set<Object> ids) {
        Field<?> primaryKey = entity.getPrimaryKeyField();
        Condition condition = entity.getPrimaryKeyConditionBuilder().buildFromIds(ids);
        List<SortField<?>> sortFields = sortingFactory.forLookup();

        return dslContext.select(primaryKey, entity.getLookupLabelField())
                .from(getTable())
                .where(condition)
                .orderBy(sortFields)
                .fetchMap(Record2::value1, Record2::value2);
    }

    @Override
    public Map<Object, String> fetchLabelsByFilter(String query) {
        if (StringUtils.isEmpty(query)) {
            return Collections.emptyMap();
        }

        Field<?> primaryKey = entity.getPrimaryKeyField();
        List<Field<?>> fields = Arrays.asList(entity.getTable().fields());
        Condition condition = filterFactory.forQuery(fields, query);
        List<SortField<?>> sortFields = sortingFactory.forLookup();

        return dslContext.select(primaryKey, entity.getLookupLabelField())
                .from(getTable())
                .where(condition)
                .orderBy(sortFields)
                .limit(MAX_FILTERED_ROWS)
                .fetchMap(Record2::value1, Record2::value2);
    }

    @Override
    public TableSlice fetchSlice(SliceRequest sliceRequest) {
        return listDao.fetchSlice(sliceRequest);
    }

    private Table<? extends Record> getTable() {
        return entity.getTable();
    }

    public static class Builder<TABLE extends Table<? extends Record>> {

        private final Entity<TABLE> entity;
        private final DSLContext dslContext;
        private final ListDao<TABLE> listDao;
        private FilterFactory filterFactory = null;
        private SortingFactory sortingFactory = null;

        private Builder(Entity<TABLE> entity, DSLContext dslContext, ListDao<TABLE> listDao) {
            this.entity = entity;
            this.dslContext = dslContext;
            this.listDao = listDao;
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

        public DefaultLookupDao<TABLE> build() {
            FilterFactory filterFactory = this.filterFactory;
            if (filterFactory == null) {
                filterFactory = new DefaultFilterFactory(entity.getDefaultFilterCondition(), entity.getWordConditionBuilder());
            }
            SortingFactory sortingFactory = this.sortingFactory;
            if (sortingFactory == null) {
                sortingFactory = new DefaultSortingFactory(entity.getLookupLabelField());
            }
            return new DefaultLookupDao<>(
                    entity,
                    dslContext,
                    listDao,
                    filterFactory,
                    sortingFactory
            );
        }
    }
}
