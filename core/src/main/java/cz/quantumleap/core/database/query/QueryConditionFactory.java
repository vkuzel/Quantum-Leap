package cz.quantumleap.core.database.query;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static cz.quantumleap.core.database.query.QueryUtils.ConditionOperator.AND;
import static cz.quantumleap.core.database.query.QueryUtils.ConditionOperator.OR;
import static cz.quantumleap.core.database.query.QueryUtils.joinConditions;
import static cz.quantumleap.core.database.query.QueryUtils.normalizeFieldName;

public class QueryConditionFactory {

    private enum ComparisonOperator {EQ, LT, GT, LE, GE}

    private final Function<String, Condition> wordConditionBuilder;
    private final Map<String, Field<?>> fieldMap;

    public QueryConditionFactory(Function<String, Condition> wordConditionBuilder, Map<String, Field<?>> fieldMap) {
        this.wordConditionBuilder = wordConditionBuilder;
        this.fieldMap = fieldMap;
    }

    /**
     * The method supports a simple query language.
     * <p>
     * Syntax: `[ [(] word | column_name_condition [condition_operator] [...] [)] ]`
     * <p>
     * `word` is any non-whitespaces containing word or a anything enclosed in double-quotes. Double-quotes can be
     * escaped.
     * <p>
     * `condition_operator` is case-insensitive OR, AND or nothing. If nothing is specified then it will be translated
     * as AND. AND has tighter binding. Conditions can be wrapped in parentheses.
     * <p>
     * `column_name_condition` is `[ column_name comparison_operator value ]` where `column_name` has to be valid column
     * name in SQL query, `comparison_operator` is one of =, >, <, <=, >= for numeric values or = for strings or
     * booleans. `value` can be wrapped in double-quotes. Invalid `column_name_condition` is considered as a set of
     * `word`.
     *
     * @param query See syntax.
     * @return Condition or null.
     */
    public Condition forQuery(String query) {
        if (StringUtils.isNotBlank(query)) {
            var tokens = tokenize(query);
            return createCondition(fieldMap, tokens);
        } else {
            return null;
        }
    }

    private Condition createCondition(Map<String, Field<?>> fieldMap, List<String> tokens) {
        return new CreateCondition(fieldMap, tokens).create();
    }

    private List<String> tokenize(String query) {
        var wordContext = false;
        var doubleQuotesContext = false;

        var tokenBegin = 0;
        var length = query.length();

        List<String> tokens = new ArrayList<>();

        for (var i = 0; i <= length; i++) {
            var previous = i > 0 ? query.charAt(i - 1) : 0;
            var current = i < length ? query.charAt(i) : 0;
            var next = i < length - 1 ? query.charAt(i + 1) : 0;

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

    private static QueryUtils.ConditionOperator resolveConditionOperator(String token) {
        token = token != null ? token.toLowerCase() : null;
        if ("and".equals(token)) {
            return AND;
        } else if ("or".equals(token)) {
            return OR;
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

    private final class CreateCondition {

        private final Map<String, Field<?>> fieldMap;
        private final List<String> tokens;
        private int startAtToken;
        private int currentTokenIndex;

        public CreateCondition(Map<String, Field<?>> fieldMap, List<String> tokens) {
            this.fieldMap = fieldMap;
            this.tokens = tokens;
        }

        public Condition create() {
            startAtToken = 0;
            return createCondition();
        }

        private Condition createCondition() {
            var size = tokens.size();

            Condition condition = null;

            for (currentTokenIndex = startAtToken; currentTokenIndex < size; currentTokenIndex++) {
                var previous = currentTokenIndex > startAtToken ? tokens.get(currentTokenIndex - 1) : null;
                var current = tokens.get(currentTokenIndex);
                var next = currentTokenIndex < size - 1 ? tokens.get(currentTokenIndex + 1) : null;
                var next2 = currentTokenIndex < size - 2 ? tokens.get(currentTokenIndex + 2) : null;
                var conditionOperator = resolveConditionOperator(previous);

                if (resolveConditionOperator(current) != null) {
                    continue;
                } else if (")".equals(current)) {
                    return condition;
                } else if ("(".equals(current)) {
                    startAtToken = ++currentTokenIndex;
                    condition = joinConditions(conditionOperator, condition, createCondition());
                    continue;
                }

                var field = fieldMap.get(normalizeFieldName(current));
                if (field != null) {
                    var comparisonOperator = resolveComparisonOperator(next);
                    if (comparisonOperator != null && next2 != null) {
                        condition = joinConditions(conditionOperator, condition, createCondition(field, comparisonOperator, next2));
                        currentTokenIndex += 2;
                    } else if (next != null) {
                        condition = joinConditions(AND, condition, createCondition(field, null, next));
                        currentTokenIndex++;
                    } else {
                        condition = joinConditions(AND, condition, wordConditionBuilder.apply(current));
                    }
                } else {
                    condition = joinConditions(resolveConditionOperator(previous), condition, wordConditionBuilder.apply(current));
                }
            }

            return condition;
        }

        private Condition createCondition(Field<?> field, ComparisonOperator comparisonOperator, String word) {
            var type = field.getType();

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
                    var bool = BooleanUtils.toBoolean(word);
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
