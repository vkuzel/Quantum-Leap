package cz.quantumleap.core.common;

import com.google.common.io.CharStreams;
import org.jooq.Condition;
import org.jooq.TableField;
import org.jooq.impl.DSL;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStreamReader;

public class Utils {

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

    public static Condition buildPerWordLikeCondition(TableField<?, String> field, String query) {
        // TODO With jOOQ 3.11 use DSL.noCondition()
        Condition condition = DSL.condition(true);
        if (org.apache.commons.lang3.StringUtils.isBlank(query)) {
            return condition;
        }

        for (String word : query.split("\\s+")) {
            String binding = Utils.escapeLikeBinding(word, '!');
            condition = condition.and(field.likeIgnoreCase(word + "%", '!')
                    .or(field.likeIgnoreCase("% " + binding + "%", '!')));
        }
        return condition;
    }

    public static String escapeLikeBinding(String binding, char escapeChar) {
        if (StringUtils.isEmpty(binding)) {
            return binding;
        }

        return binding
                .replace("%", escapeChar + "%")
                .replace("_", escapeChar + "_");
    }
}
