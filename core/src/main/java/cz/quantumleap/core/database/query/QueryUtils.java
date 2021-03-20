package cz.quantumleap.core.database.query;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.DSL;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class QueryUtils {

    public enum ConditionOperator {AND, OR}

    private static final Pattern SQL_REGEXP_SPECIAL_CHARACTERS_PATTERN = Pattern.compile("[!$()*+.:<=>?\\\\\\[\\]^{|}\\-]");

    public static String resolveDatabaseTableNameWithSchema(Table<?> table) {
        String name = table.getName();
        if (table.getSchema() != null) {
            name = table.getSchema().getName() + "." + name;
        }
        return name;
    }

    public static Table<?> resolveTableAlias(Table<?> table, Field<?> field) {
        String alias = "t_" + field.getName();
        return table.as(alias);
    }

    public static String resolveLookupFieldName(Field<?> field) {
        String fieldName = field.getName();
        if (fieldName.endsWith("_id")) {
            return StringUtils.removeEnd(fieldName, "_id");
        } else {
            return fieldName + "_lookup";
        }
    }

    public static Map<String, Field<?>> createFieldMap(List<Field<?>> fields) {
        Map<String, Field<?>> fieldMap = new HashMap<>(fields.size());
        for (Field<?> field : fields) {
            String name = normalizeFieldName(field.getName());
            fieldMap.put(name, field);
        }
        return fieldMap;
    }

    public static String normalizeFieldName(String name) {
        return name.toLowerCase();
    }

    @SafeVarargs
    public static Condition buildFindWordCondition(String word, TableField<?, String>... fields) {
        if (StringUtils.isBlank(word)) {
            return null;
        }

        Condition condition = null;
        String pattern = "(^|[^a-z0-9])" + escapeSqlRegexpBinding(word);

        for (TableField<?, String> field : fields) {
            condition = joinConditions(ConditionOperator.OR, condition, DSL.condition("{0} ~* {1}", field, pattern));
        }

        return condition;
    }

    public static Condition joinConditions(ConditionOperator operator, Condition... conditions) {
        Condition condition = null;
        for (Condition cond : conditions) {
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

        String binding = escapeSqlLikeBinding(value, '!');
        return field.likeIgnoreCase(binding + "%");
    }

    public static String escapeSqlLikeBinding(String binding, char escapeChar) {
        if (StringUtils.isBlank(binding)) {
            return binding;
        }

        String escapeString = String.valueOf(escapeChar);
        return binding
                .replace(escapeString, escapeString + escapeString)
                .replace("%", escapeChar + "%")
                .replace("_", escapeChar + "_");
    }

    public static String escapeSqlRegexpBinding(String binding) {
        if (StringUtils.isBlank(binding)) {
            return binding;
        }

        Matcher matcher = SQL_REGEXP_SPECIAL_CHARACTERS_PATTERN.matcher(binding);
        return matcher.replaceAll("\\\\$0");
    }

    public static String generateSqlBindingPlaceholders(Collection<?> collection) {
        return String.join(", ", Collections.nCopies(collection.size(), "?"));
    }

    public static Object[] createSqlBindings(Object... params) {
        List<Object> bindings = new ArrayList<>(params.length);
        for (Object param : params) {
            if (param instanceof Collection) {
                bindings.addAll((Collection<?>) param);
            } else {
                bindings.add(param);
            }
        }
        return bindings.toArray();
    }
}
