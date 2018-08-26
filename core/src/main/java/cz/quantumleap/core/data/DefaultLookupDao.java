package cz.quantumleap.core.data;

import cz.quantumleap.core.data.list.FilterBuilder;
import cz.quantumleap.core.data.primarykey.PrimaryKeyConditionBuilder;
import cz.quantumleap.core.data.transport.Slice;
import cz.quantumleap.core.data.transport.SliceRequest;
import cz.quantumleap.core.data.transport.Table.Column;
import org.apache.commons.lang3.StringUtils;
import org.jooq.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class DefaultLookupDao<TABLE extends Table<? extends Record>> implements LookupDao<TABLE> {

    private static final int MAX_FILTERED_ROWS = 10;

    private final TABLE table;
    private final Field<String> labelField;
    private final DSLContext dslContext;

    private final PrimaryKeyConditionBuilder primaryKeyConditionBuilder;
    private final FilterBuilder filterBuilder;

    private final ListDao<TABLE> listDao;

    public DefaultLookupDao(TABLE table, Field<String> labelField, DSLContext dslContext, PrimaryKeyConditionBuilder primaryKeyConditionBuilder, FilterBuilder filterBuilder, ListDao<TABLE> listDao) {
        this.table = table;
        this.labelField = labelField;
        this.dslContext = dslContext;

        this.primaryKeyConditionBuilder = primaryKeyConditionBuilder;
        this.filterBuilder = filterBuilder;

        this.listDao = listDao;
    }

    public TABLE getTable() {
        return table;
    }

    public String fetchLabelById(Object id) {
        Condition condition = primaryKeyConditionBuilder.buildFromId(id);

        return dslContext.select(labelField)
                .from(table)
                .where(condition)
                .orderBy(labelField.asc())
                .fetchOneInto(String.class);
    }

    public Map<Object, String> fetchLabelsById(Set<Object> ids) {
        Field<Object> primaryKey = primaryKeyConditionBuilder.getPrimaryKeyField();
        Condition condition = primaryKeyConditionBuilder.buildFromIds(ids);

        return dslContext.select(primaryKey, labelField)
                .from(table)
                .where(condition)
                .orderBy(labelField.asc())
                .fetchMap(Record2::value1, Record2::value2);
    }

    @Override
    public Map<Object, String> fetchLabelsByFilter(String query) {
        if (StringUtils.isEmpty(query)) {
            return Collections.emptyMap();
        }

        Field<Object> primaryKey = primaryKeyConditionBuilder.getPrimaryKeyField();
        Condition condition = filterBuilder.buildForQuery(query);

        return dslContext.select(primaryKey, labelField)
                .from(table)
                .where(condition)
                .orderBy(labelField.asc())
                .limit(MAX_FILTERED_ROWS)
                .fetchMap(Record2::value1, Record2::value2);
    }

    @Override
    public Slice<Map<Column, Object>> fetchSlice(SliceRequest sliceRequest) {
        return listDao.fetchSlice(sliceRequest);
    }

    @Override
    public <T> List<T> fetchList(SliceRequest sliceRequest, Class<T> type) {
        return listDao.fetchList(sliceRequest, type);
    }
}
