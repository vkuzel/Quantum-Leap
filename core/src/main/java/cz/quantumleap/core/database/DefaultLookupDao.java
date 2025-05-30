package cz.quantumleap.core.database;

import cz.quantumleap.core.database.domain.FetchParams;
import cz.quantumleap.core.database.domain.Slice;
import cz.quantumleap.core.database.entity.Entity;
import cz.quantumleap.core.database.query.QueryConditionFactory;
import cz.quantumleap.core.database.query.QueryUtils;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record2;
import org.jooq.Table;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static cz.quantumleap.core.database.query.QueryUtils.ConditionOperator.AND;
import static cz.quantumleap.core.utils.Strings.isBlank;

public final class DefaultLookupDao<TABLE extends Table<? extends Record>> implements LookupDao<TABLE> {

    private static final int MAX_FILTERED_ROWS = 10;

    private final Entity<TABLE> entity;
    private final DSLContext dslContext;
    private final ListDao<TABLE> listDao;

    private final QueryConditionFactory queryConditionFactory;

    public DefaultLookupDao(Entity<TABLE> entity, DSLContext dslContext, ListDao<TABLE> listDao) {
        this.entity = entity;
        this.dslContext = dslContext;
        this.listDao = listDao;

        this.queryConditionFactory = new QueryConditionFactory(
                entity.getWordConditionBuilder(),
                entity.getFieldMap()
        );
    }

    @Override
    public Entity<?> getLookupEntity() {
        return entity;
    }

    public String fetchLabelById(Object id) {
        var condition = entity.getPrimaryKeyConditionBuilder().buildFromId(id);
        var sortFields = entity.getLookupOrderBy();

        return dslContext.select(entity.getLookupLabelField())
                .from(entity.getTable())
                .where(condition)
                .orderBy(sortFields)
                .fetchOneInto(String.class);
    }

    public Map<Object, String> fetchLabelsById(Set<Object> ids) {
        var primaryKey = entity.getPrimaryKeyField();
        var condition = entity.getPrimaryKeyConditionBuilder().buildFromIds(ids);
        var sortFields = entity.getLookupOrderBy();

        return dslContext.select(primaryKey, entity.getLookupLabelField())
                .from(entity.getTable())
                .where(condition)
                .orderBy(sortFields)
                .fetchMap(Record2::value1, Record2::value2);
    }

    @Override
    public Map<Object, String> fetchLabelsByFilter(String query) {
        if (isBlank(query)) {
            return Collections.emptyMap();
        }

        var primaryKey = entity.getPrimaryKeyField();
        var condition = QueryUtils.joinConditions(
                AND,
                entity.getCondition(),
                queryConditionFactory.forQuery(query)
        );
        var sortFields = entity.getLookupOrderBy();

        return dslContext.select(primaryKey, entity.getLookupLabelField())
                .from(entity.getTable())
                .where(condition)
                .orderBy(sortFields)
                .limit(MAX_FILTERED_ROWS)
                .fetchMap(Record2::value1, Record2::value2);
    }

    @Override
    public Slice fetchSlice(FetchParams fetchParams) {
        return listDao.fetchSlice(fetchParams);
    }
}
