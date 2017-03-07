package cz.quantumleap.admin.menu;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.net.URISyntaxException;
import java.util.List;

public class AdminMenuItem {

    private final RequestMappingInfo requestMappingInfo;
    private final AdminMenuItemDefinition adminMenuItemDefinition;
    private final PreAuthorize preAuthorize;
    private final List<AdminMenuItem> children;

    public AdminMenuItem(
            RequestMappingInfo requestMappingInfo,
            AdminMenuItemDefinition adminMenuItemDefinition,
            PreAuthorize preAuthorize,
            List<AdminMenuItem> children
    ) {
        this.requestMappingInfo = requestMappingInfo;
        this.adminMenuItemDefinition = adminMenuItemDefinition;
        this.preAuthorize = preAuthorize;
        this.children = children;
    }

    public String getPath() throws URISyntaxException {
        return requestMappingInfo.getPatternsCondition().getPatterns().stream()
                .findFirst()
                .orElse("/");
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

    public List<AdminMenuItem> getChildren() {
        return children;
    }

    public static Builder fromMenuItem(AdminMenuItem adminMenuItem) {
        Builder builder = new Builder();
        builder.requestMappingInfo = adminMenuItem.requestMappingInfo;
        builder.adminMenuItemDefinition = adminMenuItem.adminMenuItemDefinition;
        builder.preAuthorize = adminMenuItem.preAuthorize;
        builder.children = adminMenuItem.children;
        return builder;
    }

    public static class Builder {
        private  RequestMappingInfo requestMappingInfo;
        private  AdminMenuItemDefinition adminMenuItemDefinition;
        private  PreAuthorize preAuthorize;
        private List<AdminMenuItem> children;

        public Builder setChildren(List<AdminMenuItem> children) {
            this.children = children;
            return this;
        }

        public AdminMenuItem build() {
            return new AdminMenuItem(requestMappingInfo, adminMenuItemDefinition, preAuthorize, children);
        }
    }
}
