package cz.quantumleap.server.admin.menu;

import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.net.URISyntaxException;
import java.util.List;

public class AdminMenuItem {

    private final RequestMappingInfo requestMappingInfo;
    private final AdminMenuItemDefinition adminMenuItemDefinition;

    private final List<AdminMenuItem> children;

    AdminMenuItem(RequestMappingInfo requestMappingInfo, AdminMenuItemDefinition adminMenuItemDefinition, List<AdminMenuItem> children) {
        this.requestMappingInfo = requestMappingInfo;
        this.adminMenuItemDefinition = adminMenuItemDefinition;
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

    public List<AdminMenuItem> getChildren() {
        return children;
    }
}
