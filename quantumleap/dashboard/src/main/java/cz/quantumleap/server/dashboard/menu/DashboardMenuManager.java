package cz.quantumleap.server.dashboard.menu;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class DashboardMenuManager {

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    private List<DashboardMenuItem> menuItems = Collections.emptyList();

    public DashboardMenuManager(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    }

    public List<DashboardMenuItem> getMenuItems() {
        return menuItems;
    }

    // TODO More like listener on controllers-initialised or something like that!
    @PostConstruct
    private void buildMenu() {
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
        menuItems = handlerMethods.entrySet().stream()
                .map(this::convertToMenuItem)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private DashboardMenuItem convertToMenuItem(Map.Entry<RequestMappingInfo, HandlerMethod> handlerMethod) {
        Method method = handlerMethod.getValue().getMethod();
        DashboardMenuItemDefinition dashboardMenuItemDefinition = AnnotationUtils.findAnnotation(method, DashboardMenuItemDefinition.class);
        if (dashboardMenuItemDefinition != null) {
            RequestMappingInfo requestMappingInfo = handlerMethod.getKey();

            return new DashboardMenuItem(requestMappingInfo, dashboardMenuItemDefinition, Collections.emptyList());
        }

        // TODO Construct tree through map?
        // Map<MenuItem, Integer> item, level
        // Map<String, MenuItem> mapping, item
        // TODO How to define parent?

        return null;
    }

}
