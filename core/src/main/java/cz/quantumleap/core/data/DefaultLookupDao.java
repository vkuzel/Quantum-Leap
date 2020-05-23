package cz.quantumleap.core.data;

import cz.quantumleap.core.data.entity.Entity;
import cz.quantumleap.core.data.entity.EntityIdentifier;
import cz.quantumleap.core.data.transport.Slice;
import cz.quantumleap.core.data.transport.SliceRequest;
import cz.quantumleap.core.data.transport.Table.Column;
import org.apache.commons.lang3.StringUtils;
import org.jooq.*;

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
    public EntityIdentifier<TABLE> getLookupEntityIdentifier() {
        return entity.getIdentifier();
    }

    public String fetchLabelById(Object id) {
        Condition condition = entity.getPrimaryKeyConditionBuilder().buildFromId(id);

        return dslContext.select(entity.getLookupLabelField())
                .from(getTable())
                .where(condition)
                .orderBy(entity.getSortingBuilder().buildForLookup())
                .fetchOneInto(String.class);
    }

    public Map<Object, String> fetchLabelsById(Set<Object> ids) {
        Field<Object> primaryKey = entity.getPrimaryKeyConditionBuilder().getPrimaryKeyField();
        Condition condition = entity.getPrimaryKeyConditionBuilder().buildFromIds(ids);

        return dslContext.select(primaryKey, entity.getLookupLabelField())
                .from(getTable())
                .where(condition)
                .orderBy(entity.getSortingBuilder().buildForLookup())
                .fetchMap(Record2::value1, Record2::value2);
    }

    @Override
    public Map<Object, String> fetchLabelsByFilter(String query) {
        if (StringUtils.isEmpty(query)) {
            return Collections.emptyMap();
        }

        Field<Object> primaryKey = entity.getPrimaryKeyConditionBuilder().getPrimaryKeyField();
        Condition condition = entity.getFilterBuilder().buildForQuery(query);

        return dslContext.select(primaryKey, entity.getLookupLabelField())
                .from(getTable())
                .where(condition)
                .orderBy(entity.getSortingBuilder().buildForLookup())
                .limit(MAX_FILTERED_ROWS)
                .fetchMap(Record2::value1, Record2::value2);
    }

    @Override
    public Slice<Map<Column, Object>> fetchSlice(SliceRequest sliceRequest) {
        return listDao.fetchSlice(sliceRequest);
    }

    private Table<? extends Record> getTable() {
        return entity.getTable();
    }
}
