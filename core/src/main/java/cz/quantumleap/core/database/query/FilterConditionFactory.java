package cz.quantumleap.core.database.query;

import org.jooq.Condition;
import org.jooq.Field;

import java.util.Map;

import static cz.quantumleap.core.database.query.QueryUtils.getFieldSafely;
import static cz.quantumleap.core.database.query.QueryUtils.normalizeFieldName;

public class FilterConditionFactory {

    private final Map<String, Field<?>> fieldMap;

    public FilterConditionFactory(Map<String, Field<?>> fieldMap) {
        this.fieldMap = fieldMap;
    }

    public Condition forFilter(Map<String, Object> filter) {
        Condition resultCondition = null;

        for (Map.Entry<String, Object> fieldNameValue : filter.entrySet()) {
            String fieldName = normalizeFieldName(fieldNameValue.getKey());
            Field<Object> field = getFieldSafely(fieldMap, fieldName);
            Object value = fieldNameValue.getValue();

            Condition condition = field.eq(value);
            if (resultCondition == null) {
                resultCondition = condition;
            } else {
                resultCondition = resultCondition.and(condition);
            }
        }

        return resultCondition;
    }
}
