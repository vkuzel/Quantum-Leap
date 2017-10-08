package cz.quantumleap.admin.menu;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AdminMenuItem {

    public enum State {
        NONE, OPEN, ACTIVE
    }

    private final RequestMappingInfo requestMappingInfo;
    private final AdminMenuItemDefinition adminMenuItemDefinition;
    private final PreAuthorize preAuthorize;
    private final State state;
    private final List<AdminMenuItem> children;

    public AdminMenuItem(
            RequestMappingInfo requestMappingInfo,
            AdminMenuItemDefinition adminMenuItemDefinition,
            PreAuthorize preAuthorize,
            State state,
            List<AdminMenuItem> children
    ) {
        this.requestMappingInfo = requestMappingInfo;
        this.adminMenuItemDefinition = adminMenuItemDefinition;
        this.preAuthorize = preAuthorize;
        this.state = state;
        this.children = children;
    }

    public String getPath() {
        return requestMappingInfo.getPatternsCondition().getPatterns().stream()
                .findFirst()
                .orElse("/");
    }

    public Set<String> getPaths() {
        return requestMappingInfo.getPatternsCondition().getPatterns();
    }

    public String getTitle() {
        return adminMenuItemDefinition.title();
    }

    public String getFontAwesomeIcon() {
        return adminMenuItemDefinition.fontAwesomeIcon();
    }

    public String getSecurityExpression() {
        return preAuthorize != null ? preAuthorize.value() : null;
    }

    public State getState() {
        return state;
    }

    public List<AdminMenuItem> getChildren() {
        return children;
    }

    public static Builder fromMenuItem(AdminMenuItem adminMenuItem) {
        Builder builder = new Builder();
        builder.requestMappingInfo = adminMenuItem.requestMappingInfo;
        builder.adminMenuItemDefinition = adminMenuItem.adminMenuItemDefinition;
        builder.preAuthorize = adminMenuItem.preAuthorize;
        builder.state = adminMenuItem.state;
        builder.children = new ArrayList<>(adminMenuItem.children);
        return builder;
    }

    public static class Builder {
        private RequestMappingInfo requestMappingInfo;
        private AdminMenuItemDefinition adminMenuItemDefinition;
        private PreAuthorize preAuthorize;
        private List<AdminMenuItem> children;
        private State state;

        public Builder setChildren(List<AdminMenuItem> children) {
            this.children = children;
            return this;
        }

        public Builder setState(State state) {
            this.state = state;
            return this;
        }

        public AdminMenuItem build() {
            return new AdminMenuItem(requestMappingInfo, adminMenuItemDefinition, preAuthorize, state, children);
        }
    }
}
