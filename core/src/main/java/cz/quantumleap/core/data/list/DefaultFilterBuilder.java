package cz.quantumleap.core.data.list;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;

import java.util.Map;
import java.util.function.Function;

public class DefaultFilterBuilder implements FilterBuilder {

    private final Table<? extends Record> table;
    private final Function<String, Condition> queryFilterConditionBuilder;

    // TODO Merge query filter with standard filter. Invent/find query language that will allow write standard filter as a string.
    public DefaultFilterBuilder(Table<? extends Record> table, Function<String, Condition> queryFilterConditionBuilder) {
        this.table = table;
        this.queryFilterConditionBuilder = queryFilterConditionBuilder;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Condition buildForFilter(Map<String, Object> filter) {
        Condition lastCondition = null;

        for (Map.Entry<String, Object> fieldNameValue : filter.entrySet()) {
            String fieldName = fieldNameValue.getKey();
            Field<Object> field = (Field<Object>) table.field(fieldName);

            Validate.notNull(field, "Field %s not found in table %s", fieldName, table.getName());

            Condition condition = field.eq(fieldNameValue.getValue());
            if (lastCondition == null) {
                lastCondition = condition;
            } else {
                lastCondition = lastCondition.and(condition);
            }
        }

        return lastCondition;
    }

    @Override
    public Condition buildForQuery(String query) {
        if (StringUtils.isEmpty(query)) {
            return null;
        }

        return queryFilterConditionBuilder.apply(query);
    }
}
