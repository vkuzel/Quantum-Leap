package cz.quantumleap.core.common;

import com.google.common.io.CharStreams;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.TableField;
import org.springframework.core.io.Resource;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStreamReader;

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
        String binding = Utils.escapeLikeBinding(word, '!');

        for (TableField<?, String> field : fields) {
            condition = joinConditions(ConditionOperator.OR, condition, field.likeIgnoreCase(word + "%", '!'), field.likeIgnoreCase("% " + binding + "%", '!'));
        }

        return condition;
    }

    public static Condition joinConditions(ConditionOperator operator,  Condition... conditions) {
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

    public static String escapeLikeBinding(String binding, char escapeChar) {
        if (StringUtils.isBlank(binding)) {
            return binding;
        }

        return binding
                .replace("%", escapeChar + "%")
                .replace("_", escapeChar + "_");
    }
}
