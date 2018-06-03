package cz.quantumleap.core.data.list;

import cz.quantumleap.core.common.Utils;
import cz.quantumleap.core.common.Utils.ConditionOperator;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.math.NumberUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.DSL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class DefaultFilterBuilder implements FilterBuilder {

    private enum ComparisonOperator {EQ, LT, GT, LE, GE}

    private final Table<? extends Record> table;
    private final Condition defaultCondition;
    private final Function<String, Condition> wordConditionBuilder;

    public DefaultFilterBuilder(Table<? extends Record> table, Function<String, Condition> wordConditionBuilder) {
        this(table, null, wordConditionBuilder);
    }

    public DefaultFilterBuilder(Table<? extends Record> table, Condition defaultCondition, Function<String, Condition> wordConditionBuilder) {
        this.table = table;
        this.defaultCondition = defaultCondition;
        this.wordConditionBuilder = wordConditionBuilder;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Condition buildForFilter(Map<String, Object> filter) {
        Condition resultCondition = null;

        for (Map.Entry<String, Object> fieldNameValue : filter.entrySet()) {
            String fieldName = fieldNameValue.getKey();
            Field<Object> field = (Field<Object>) table.field(fieldName);

            Validate.notNull(field, "Field %s was not found in table %s", fieldName, table.getName());

            Condition condition = field.eq(fieldNameValue.getValue());
            if (resultCondition == null) {
                resultCondition = condition;
            } else {
                resultCondition = resultCondition.and(condition);
            }
        }

        return Utils.joinConditions(ConditionOperator.AND, defaultCondition, resultCondition);
    }

    /**
     * The method supports a simple query language.
     *
     * Syntax: `[ [(] word | column_name_condition [condition_operator] [...] [)] ]`
     *
     * `word` is any non-whitespaces containing word or a anything enclosed in double-quotes. Double-quotes can be
     * escaped.
     *
     * `condition_operator` is case-insensitive OR, AND or nothing. If nothing is specified then it will be translated
     * as AND. AND has tighter binding. Conditions can be wrapped in parentheses.
     *
     * `column_name_condition` is `[ column_name comparison_operator value ]` where `column_name` has to be valid column
     * name in SQL query, `comparison_operator` is one of =, >, <, <=, >= for numeric values or = for strings or
     * booleans. `value` can be wrapped in double-quotes. Invalid `column_name_condition` is considered as a set of
     * `word`.
     *
     * @param query See syntax.
     * @return Condition or null.
     */
    @Override
    public Condition buildForQuery(String query) {
        Condition condition = null;

        if (StringUtils.isNotBlank(query)) {
            List<String> tokens = tokenize(query);
            condition = createCondition(tokens);
        }

        return Utils.joinConditions(ConditionOperator.AND, defaultCondition, condition);
    }

    Condition createCondition(List<String> tokens) {
        return new CreateCondition(tokens).create();
    }

    List<String> tokenize(String query) {
        boolean wordContext = false;
        boolean doubleQuotesContext = false;

        int tokenBegin = 0;
        int length = query.length();

        List<String> tokens = new ArrayList<>();

        for (int i = 0; i <= length; i++) {
            char previous = i > 0 ? query.charAt(i - 1) : 0;
            char current = i < length ? query.charAt(i) : 0;
            char next = i < length - 1 ? query.charAt(i + 1) : 0;

            if (doubleQuotesContext && (current == '"' || current == 0)) {
                doubleQuotesContext = false;
                tokens.add(query.substring(tokenBegin, i));
            } else if (current == '"' && previous != '\\') {
                if (wordContext) {
                    tokens.add(query.substring(tokenBegin, i));
                    wordContext = false;
                }
                doubleQuotesContext = true;
                tokenBegin = i + 1;
            } else if (doubleQuotesContext) {
            } else if (current == ' ' || current == '=' || current == '>' || current == '<' || current == '(' || current == ')') {
                if (wordContext) {
                    tokens.add(query.substring(tokenBegin, i));
                    wordContext = false;
                }
                if (current == ' ') {
                } else if ((current == '>' || current == '<') && next == '=') {
                    tokens.add(query.substring(i, ++i + 1));
                } else {
                    tokens.add(query.substring(i, i + 1));
                }
            } else if (!wordContext) {
                tokenBegin = i;
                wordContext = true;
            } else if (i >= length) {
                tokens.add(query.substring(tokenBegin, length));
            }
        }

        return tokens;
    }

    private static ConditionOperator resolveConditionOperator(String token) {
        token = token != null ? token.toLowerCase() : null;
        if ("and".equals(token)) {
            return ConditionOperator.AND;
        } else if ("or".equals(token)) {
            return ConditionOperator.OR;
        } else {
            return null;
        }
    }

    private static ComparisonOperator resolveComparisonOperator(String token) {
        if (token == null) {
            return null;
        }

        switch (token) {
            case "=":
                return ComparisonOperator.EQ;
            case "<":
                return ComparisonOperator.LT;
            case ">":
                return ComparisonOperator.GT;
            case "<=":
                return ComparisonOperator.LE;
            case ">=":
                return ComparisonOperator.GE;
            default:
                return null;
        }
    }

    private class CreateCondition {

        private final List<String> tokens;
        private int startAtToken;
        private int currentTokenIndex;

        public CreateCondition(List<String> tokens) {
            this.tokens = tokens;
        }

        public Condition create() {
            startAtToken = 0;
            return createCondition();
        }

        private Condition createCondition() {
            int size = tokens.size();

            Condition condition = null;

            for (currentTokenIndex = startAtToken; currentTokenIndex < size; currentTokenIndex++) {
                String previous = currentTokenIndex > startAtToken ? tokens.get(currentTokenIndex - 1) : null;
                String current = tokens.get(currentTokenIndex);
                String next = currentTokenIndex < size - 1 ? tokens.get(currentTokenIndex + 1) : null;
                String next2 = currentTokenIndex < size - 2 ? tokens.get(currentTokenIndex + 2) : null;
                ConditionOperator conditionOperator = resolveConditionOperator(previous);

                if (resolveConditionOperator(current) != null) {
                    continue;
                } else if (")".equals(current)) {
                    return condition;
                } else if ("(".equals(current)) {
                    startAtToken = ++currentTokenIndex;
                    condition = Utils.joinConditions(conditionOperator, condition, createCondition());
                    continue;
                }

                Field<?> field = table.field(current);
                if (field != null) {
                    ComparisonOperator comparisonOperator = resolveComparisonOperator(next);
                    if (comparisonOperator != null && next2 != null) {
                        condition = Utils.joinConditions(conditionOperator, condition, createCondition(field, comparisonOperator, next2));
                        currentTokenIndex += 2;
                    } else if (next != null) {
                        condition = Utils.joinConditions(ConditionOperator.AND, condition, createCondition(field, null, next));
                        currentTokenIndex++;
                    } else {
                        condition = Utils.joinConditions(ConditionOperator.AND, condition, wordConditionBuilder.apply(current));
                    }
                } else {
                    condition = Utils.joinConditions(resolveConditionOperator(previous), condition, wordConditionBuilder.apply(current));
                }
            }

            return condition;
        }

        private Condition createCondition(Field<?> field, ComparisonOperator comparisonOperator, String word) {
            Class<?> type = field.getType();

            if (Number.class.isAssignableFrom(type) && NumberUtils.isCreatable(word)) {
                if (type == Integer.class) {
                    return createNumericFieldCondition(field.cast(Integer.class), comparisonOperator, NumberUtils.createInteger(word));
                } else if (type == Long.class) {
                    return createNumericFieldCondition(field.cast(Long.class), comparisonOperator, NumberUtils.createLong(word));
                } else if (type == Float.class) {
                    return createNumericFieldCondition(field.cast(Float.class), comparisonOperator, NumberUtils.createFloat(word));
                } else if (type == Double.class) {
                    return createNumericFieldCondition(field.cast(Double.class), comparisonOperator, NumberUtils.createDouble(word));
                }
            } else if (type == Boolean.class) {
                if (comparisonOperator == ComparisonOperator.EQ) {
                    boolean bool = BooleanUtils.toBoolean(word);
                    return field.cast(Boolean.class).eq(bool);
                }
            } else if (type == String.class) {
                if (comparisonOperator == ComparisonOperator.EQ) {
                    return field.cast(String.class).eq(word);
                }
            }

            return DSL.condition(false);
        }

        private <T extends Number> Condition createNumericFieldCondition(Field<T> field, ComparisonOperator comparisonOperator, T value) {
            switch (comparisonOperator) {
                case EQ:
                    return field.eq(value);
                case LT:
                    return field.lt(value);
                case GT:
                    return field.gt(value);
                case LE:
                    return field.le(value);
                case GE:
                    return field.ge(value);
            }

            return DSL.condition(false);
        }
    }
}
