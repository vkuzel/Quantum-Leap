package cz.quantumleap.core.data;

import com.google.common.collect.ImmutableMap;
import cz.quantumleap.core.data.detail.PrimaryKeyConditionBuilder;
import org.jooq.*;

import java.util.Map;
import java.util.Set;

public final class DefaultLookupDao<TABLE extends Table<? extends Record>> implements LookupDao<TABLE> {

    private final TABLE table;
    private final Field<String> labelField;
    private final DSLContext dslContext;
    private final PrimaryKeyConditionBuilder primaryKeyConditionBuilder;

    public DefaultLookupDao(TABLE table, Field<String> labelField, DSLContext dslContext, PrimaryKeyConditionBuilder primaryKeyConditionBuilder) {
        this.table = table;
        this.labelField = labelField;
        this.dslContext = dslContext;
        this.primaryKeyConditionBuilder = primaryKeyConditionBuilder;
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
        return ImmutableMap.of(
                1, "Lookup data row 1",
                2, "Lookup data row 2",
                3, "Lookup data row 3",
                4, "Filter: " + filter
        );
    }
}
