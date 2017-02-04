package cz.quantumleap.server.dashboard.menu;

import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.net.URISyntaxException;
import java.util.List;

public class DashboardMenuItem {

    private final RequestMappingInfo requestMappingInfo;
    private final DashboardMenuItemDefinition dashboardMenuItemDefinition;

    private final List<DashboardMenuItem> children;

    DashboardMenuItem(RequestMappingInfo requestMappingInfo, DashboardMenuItemDefinition dashboardMenuItemDefinition, List<DashboardMenuItem> children) {
        this.requestMappingInfo = requestMappingInfo;
        this.dashboardMenuItemDefinition = dashboardMenuItemDefinition;
        this.children = children;
    }

    public String getPath() throws URISyntaxException {
        return requestMappingInfo.getPatternsCondition().getPatterns().stream()
                .findFirst()
                .orElse("/");
    }

    public String getTitle() {
        return dashboardMenuItemDefinition.title();
    }

    public String getFontAwesomeIcon() {
        return dashboardMenuItemDefinition.fontAwesomeIcon();
    }

    public List<DashboardMenuItem> getChildren() {
        return children;
    }
}
