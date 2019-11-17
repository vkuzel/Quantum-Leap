package cz.quantumleap.core.common;

import com.google.common.io.CharStreams;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.TableField;
import org.jooq.impl.DSL;
import org.springframework.core.io.Resource;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Utils {

    public enum ConditionOperator {AND, OR}

    private static final String AJAX_HEADER_NAME = "X-Requested-With";
    private static final String AJAX_HEADER_VALUE = "XMLHttpRequest";

    public static String readResourceToString(Resource resource) {
        try (InputStreamReader reader = new InputStreamReader(resource.getInputStream())) {
            return CharStreams.toString(reader);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static boolean isAjaxRequest(HttpServletRequest request) {
        return AJAX_HEADER_VALUE.equals(request.getHeader(AJAX_HEADER_NAME));
    }

    @SafeVarargs
    public static Condition buildFindWordCondition(String word, TableField<?, String>... fields) {
        if (StringUtils.isBlank(word)) {
            return null;
        }

        Condition condition = null;
        String binding = Utils.escapeSqlLikeBinding(word, '!');

        for (TableField<?, String> field : fields) {
            condition = joinConditions(ConditionOperator.OR, condition, field.likeIgnoreCase(word + "%", '!'), field.likeIgnoreCase("% " + binding + "%", '!'));
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

    public static String generateSqlBindingPlaceholders(Collection<?> collection) {
        return String.join(", ", Collections.nCopies(collection.size(), "?"));
    }

    public static Object[] createSqlBindings(Object... params) {
        List<Object> bindings = new ArrayList<>(params.length);
        for (Object param : params) {
            if (param instanceof Collection) {
                bindings.addAll((Collection) param);
            } else {
                bindings.add(param);
            }
        }
        return bindings.toArray();
    }

    /**
     * Null is more than any other value.
     */
    @SafeVarargs
    public static <T extends Comparable<? super T>> T min(T... items) {
        T min = null;
        for (T item : items) {
            if (item != null) {
                if (min == null) {
                    min = item;
                } else if (item.compareTo(min) < 0) {
                    min = item;
                }
            }
        }
        return min;
    }

    /**
     * Null is less than any other value.
     */
    @SafeVarargs
    public static <T extends Comparable<? super T>> T max(T... items) {
        T max = null;
        for (T item : items) {
            if (item != null) {
                if (max == null) {
                    max = item;
                } else if (item.compareTo(max) > 0) {
                    max = item;
                }
            }
        }
        return max;
    }
}
