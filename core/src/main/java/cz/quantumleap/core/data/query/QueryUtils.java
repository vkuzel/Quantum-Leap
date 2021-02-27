package cz.quantumleap.core.data.query;

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

    @SuppressWarnings("unused")
    public static String resolveDatabaseTableNameWithSchema(Table<?> table) {
        String name = table.getName();
        if (table.getSchema() != null) {
            name = table.getSchema().getName() + "." + name;
        }
        return name;
    }

    @SuppressWarnings("unused")
    public static Table<?> resolveTableAlias(Table<?> table, Field<?> field) {
        String alias = "t_" + field.getName();
        return table.as(alias);
    }

    @SuppressWarnings("unused")
    public static String resolveLookupIdFieldName(Field<?> field) {
        return field.getName() + ".id";
    }

    @SuppressWarnings("unused")
    public static Map<String, Field<?>> createFieldMap(List<Field<?>> fields) {
        Map<String, Field<?>> fieldMap = new HashMap<>(fields.size());
        for (Field<?> field : fields) {
            String name = normalizeFieldName(field.getName());
            fieldMap.put(name, field);
        }
        return fieldMap;
    }

    @SuppressWarnings("unused")
    public static String normalizeFieldName(String name) {
        return name.toLowerCase();
    }

    @SafeVarargs
    @SuppressWarnings("unused")
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

    @SuppressWarnings("unused")
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

    @SuppressWarnings("unused")
    public static Condition startsWithIgnoreCase(Field<String> field, String value) {
        if (StringUtils.isBlank(value)) {
            return DSL.falseCondition();
        }

        String binding = escapeSqlLikeBinding(value, '!');
        return field.likeIgnoreCase(binding + "%");
    }

    @SuppressWarnings("unused")
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

    @SuppressWarnings("unused")
    public static String escapeSqlRegexpBinding(String binding) {
        if (StringUtils.isBlank(binding)) {
            return binding;
        }

        Matcher matcher = SQL_REGEXP_SPECIAL_CHARACTERS_PATTERN.matcher(binding);
        return matcher.replaceAll("\\\\$0");
    }

    @SuppressWarnings("unused")
    public static String generateSqlBindingPlaceholders(Collection<?> collection) {
        return String.join(", ", Collections.nCopies(collection.size(), "?"));
    }

    @SuppressWarnings("unused")
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
