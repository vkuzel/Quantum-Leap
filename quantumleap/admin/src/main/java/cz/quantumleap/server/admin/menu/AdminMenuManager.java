package cz.quantumleap.server.admin.menu;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.prepost.PreAuthorize;
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
public class AdminMenuManager {

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    private List<AdminMenuItem> menuItems = Collections.emptyList();

    public AdminMenuManager(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    }

    public List<AdminMenuItem> getMenuItems() {
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

    private AdminMenuItem convertToMenuItem(Map.Entry<RequestMappingInfo, HandlerMethod> handlerMethod) {
        Method method = handlerMethod.getValue().getMethod();
        AdminMenuItemDefinition adminMenuItemDefinition = AnnotationUtils.findAnnotation(method, AdminMenuItemDefinition.class);
        if (adminMenuItemDefinition != null) {
            RequestMappingInfo requestMappingInfo = handlerMethod.getKey();
            PreAuthorize preAuthorize = findPreAuthorize(handlerMethod.getValue());

            return new AdminMenuItem(requestMappingInfo, adminMenuItemDefinition, preAuthorize, Collections.emptyList());
        }

        // TODO Construct tree through map?
        // Map<MenuItem, Integer> item, level
        // Map<String, MenuItem> mapping, item
        // TODO How to define parent?

        return null;
    }

    private PreAuthorize findPreAuthorize(HandlerMethod handlerMethod) {
        Class<?> beanType = handlerMethod.getBeanType();
        Method method = handlerMethod.getMethod();

        PreAuthorize methodPreAuthorize = AnnotationUtils.findAnnotation(method, PreAuthorize.class);
        PreAuthorize typePreAuthorize = AnnotationUtils.findAnnotation(beanType, PreAuthorize.class);

        return methodPreAuthorize != null ? methodPreAuthorize :
                (typePreAuthorize != null ? typePreAuthorize : null);
    }
}
