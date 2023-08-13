package cz.quantumleap.core.database.query;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Record;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.util.*;
import java.util.regex.Pattern;

public final class QueryUtils {

    public enum ConditionOperator {AND, OR}

    private static final Pattern SQL_REGEXP_SPECIAL_CHARACTERS_PATTERN = Pattern.compile("[!$()*+.:<=>?\\\\\\[\\]^{|}\\-]");

    public static String resolveDatabaseTableNameWithSchema(Table<?> table) {
        var name = table.getName();
        if (table.getSchema() != null) {
            name = table.getSchema().getName() + "." + name;
        }
        return name;
    }

    public static Table<?> resolveTableAlias(Table<?> table, Field<?> field) {
        var alias = "t_" + field.getName();
        return table.as(alias);
    }

    public static String resolveLookupFieldName(Field<?> field) {
        var fieldName = field.getName();
        if (fieldName.endsWith("_id")) {
            return StringUtils.removeEnd(fieldName, "_id");
        } else {
            return fieldName + "_lookup";
        }
    }

    public static Map<String, Field<?>> createFieldMap(Field<?>[] fields) {
        return createFieldMap(Arrays.asList(fields));
    }

    public static Map<String, Field<?>> createFieldMap(List<Field<?>> fields) {
        Map<String, Field<?>> fieldMap = new HashMap<>(fields.size());
        for (var field : fields) {
            var name = normalizeFieldName(field.getName());
            fieldMap.put(name, field);
        }
        return fieldMap;
    }

    public static String normalizeFieldName(String name) {
        return name.toLowerCase();
    }

    @SuppressWarnings("unchecked")
    public static Field<Object> getFieldSafely(Map<String, Field<?>> fieldMap, String fieldName) {
        var normalized = normalizeFieldName(fieldName);
        var field = fieldMap.get(normalized);
        if (field == null) {
            var names = String.join(", ", fieldMap.keySet());
            throw new IllegalArgumentException("Field" + normalized + " not found in " + names);
        }
        return (Field<Object>) field;
    }

    @SafeVarargs
    public static Condition buildFindWordCondition(String word, Field<String>... fields) {
        if (StringUtils.isBlank(word)) {
            return null;
        }

        Condition condition = null;
        var pattern = "(^|[^a-z0-9])" + escapeSqlRegexpBinding(word);

        for (var field : fields) {
            condition = joinConditions(ConditionOperator.OR, condition, DSL.condition("unaccent({0}) ~* unaccent({1})", field, pattern));
        }

        return condition;
    }

    public static Condition joinConditions(ConditionOperator operator, Condition... conditions) {
        Condition condition = null;
        for (var cond : conditions) {
            if (cond == null) {
            } else if (condition == null) {
                condition = cond;
            } else if (operator == ConditionOperator.OR) {
                condition = condition.or(cond);
            } else {
                condition = condition.and(cond);
            }
        }
        return condition;
    }

    public static Condition startsWithIgnoreCase(Field<String> field, String value) {
        if (StringUtils.isBlank(value)) {
            return DSL.falseCondition();
        }

        var binding = escapeSqlLikeBinding(value, '!');
        return field.likeIgnoreCase(binding + "%");
    }

    public static String escapeSqlLikeBinding(String binding, char escapeChar) {
        if (StringUtils.isBlank(binding)) {
            return binding;
        }

        var escapeString = String.valueOf(escapeChar);
        return binding
                .replace(escapeString, escapeString + escapeString)
                .replace("%", escapeChar + "%")
                .replace("_", escapeChar + "_");
    }

    public static String escapeSqlRegexpBinding(String binding) {
        if (StringUtils.isBlank(binding)) {
            return binding;
        }

        var matcher = SQL_REGEXP_SPECIAL_CHARACTERS_PATTERN.matcher(binding);
        return matcher.replaceAll("\\\\$0");
    }

    public static String generateSqlBindingPlaceholders(Collection<?> collection) {
        return String.join(", ", Collections.nCopies(collection.size(), "?"));
    }

    public static Object[] createSqlBindings(Object... params) {
        List<Object> bindings = new ArrayList<>(params.length);
        for (var param : params) {
            if (param instanceof Collection) {
                bindings.addAll((Collection<?>) param);
            } else {
                bindings.add(param);
            }
        }
        return bindings.toArray();
    }

    public static RecordMapper<Record, String> stringFieldMapper(int index) {
        return record -> record.get(index, String.class);
    }

    public static RecordMapper<Record, Integer> integerFieldMapper(int index) {
        return record -> record.get(index, Integer.class);
    }
}
