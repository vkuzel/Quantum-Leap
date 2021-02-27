package cz.quantumleap.admin.menu;

import cz.quantumleap.core.view.WebUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class AdminMenuItem {

    public enum State {
        NONE, OPEN, ACTIVE
    }

    private final List<RequestMappingInfo> requestMappingInfoList;
    private final AdminMenuItemDefinition adminMenuItemDefinition;
    private final PreAuthorize preAuthorize;
    private final State state;
    private final List<AdminMenuItem> children;

    public AdminMenuItem(
            List<RequestMappingInfo> requestMappingInfoList,
            AdminMenuItemDefinition adminMenuItemDefinition,
            PreAuthorize preAuthorize,
            State state,
            List<AdminMenuItem> children
    ) {
        this.requestMappingInfoList = requestMappingInfoList;
        this.adminMenuItemDefinition = adminMenuItemDefinition;
        this.preAuthorize = preAuthorize;
        this.state = state;
        this.children = children;
    }

    public String getPath() {
        for (RequestMappingInfo requestMappingInfo : requestMappingInfoList) {
            for (String pattern : requestMappingInfo.getPatternsCondition().getPatterns()) {
                return pattern;
            }
        }
        return "/";
    }

    public boolean matchesRequest(HttpServletRequest request) {
        WebUtils.cacheRequestPath(request);
        for (RequestMappingInfo requestMappingInfo : requestMappingInfoList) {
            if (requestMappingInfo.getMatchingCondition(request) != null) {
                return true;
            }
        }
        return false;
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
        builder.requestMappingInfoList = adminMenuItem.requestMappingInfoList;
        builder.adminMenuItemDefinition = adminMenuItem.adminMenuItemDefinition;
        builder.preAuthorize = adminMenuItem.preAuthorize;
        builder.state = adminMenuItem.state;
        builder.children = new ArrayList<>(adminMenuItem.children);
        return builder;
    }

    public static class Builder {
        private List<RequestMappingInfo> requestMappingInfoList;
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
            return new AdminMenuItem(requestMappingInfoList, adminMenuItemDefinition, preAuthorize, state, children);
        }
    }
}
