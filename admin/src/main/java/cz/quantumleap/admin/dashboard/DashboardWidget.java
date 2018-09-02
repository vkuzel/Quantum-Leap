package cz.quantumleap.admin.dashboard;

/**
 * To order the widgets within a position group, use an @Order annotation.
 */
public interface DashboardWidget<MODEL> {

    enum Position {
        TOP, LEFT, RIGHT, BOTTOM
    }

    Position getPosition();

    String getThymeleafFragmentExpression();

    ModelAttribute<MODEL> getModelModelAttribute();

    class ModelAttribute<MODEL> {

        private final String name;
        private final MODEL value;

        public ModelAttribute(String name, MODEL value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public MODEL getValue() {
            return value;
        }
    }
}
