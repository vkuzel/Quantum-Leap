package cz.quantumleap.core.data;

import cz.quantumleap.core.data.primarykey.PrimaryKeyConditionBuilder;
import cz.quantumleap.core.data.transport.Slice;
import cz.quantumleap.core.data.transport.SliceRequest;
import org.apache.commons.lang3.StringUtils;
import org.jooq.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public final class DefaultLookupDao<TABLE extends Table<? extends Record>> implements LookupDao<TABLE> {

    private static final int MAX_FILTERED_ROWS = 10;

    private final TABLE table;
    private final Field<String> labelField;
    private final DSLContext dslContext;

    private final Function<String, Condition> filterConditionBuilder;
    private final PrimaryKeyConditionBuilder primaryKeyConditionBuilder;

    private final ListDao<TABLE> listDao;

    public DefaultLookupDao(TABLE table, Field<String> labelField, DSLContext dslContext, Function<String, Condition> filterConditionBuilder, PrimaryKeyConditionBuilder primaryKeyConditionBuilder, ListDao<TABLE> listDao) {
        this.table = table;
        this.labelField = labelField;
        this.dslContext = dslContext;

        this.filterConditionBuilder = filterConditionBuilder;
        this.primaryKeyConditionBuilder = primaryKeyConditionBuilder;

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
    public Map<Object, String> fetchLabelsByFilter(String filter) {
        if (StringUtils.isEmpty(filter)) {
            return Collections.emptyMap();
        }

        Field<Object> primaryKey = primaryKeyConditionBuilder.getPrimaryKeyField();
        Condition condition = filterConditionBuilder.apply(filter);

        return dslContext.select(primaryKey, labelField)
                .from(table)
                .where(condition)
                .orderBy(labelField.asc())
                .limit(MAX_FILTERED_ROWS)
                .fetchMap(Record2::value1, Record2::value2);
    }

    @Override
    public Slice fetchSlice(SliceRequest sliceRequest) {
        return listDao.fetchSlice(sliceRequest);
    }

    @Override
    public <T> List<T> fetchList(SliceRequest sliceRequest, Class<T> type) {
        return listDao.fetchList(sliceRequest, type);
    }
}
