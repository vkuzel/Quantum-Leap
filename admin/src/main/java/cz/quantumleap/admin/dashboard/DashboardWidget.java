package cz.quantumleap.admin.dashboard;

import java.util.Map;

/**
 * To order the widgets within a position group, use an @Order annotation.
 */
public interface DashboardWidget {

    enum Position {
        TOP, LEFT, RIGHT, BOTTOM
    }

    Position getPosition();

    String getThymeleafFragmentExpression();

    Map<String, Object> getModelAttributes();
}
