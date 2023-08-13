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

        for (var fieldNameValue : filter.entrySet()) {
            var fieldName = normalizeFieldName(fieldNameValue.getKey());
            var field = getFieldSafely(fieldMap, fieldName);
            var value = fieldNameValue.getValue();

            var condition = field.eq(value);
            if (resultCondition == null) {
                resultCondition = condition;
            } else {
                resultCondition = resultCondition.and(condition);
            }
        }

        return resultCondition;
    }
}
