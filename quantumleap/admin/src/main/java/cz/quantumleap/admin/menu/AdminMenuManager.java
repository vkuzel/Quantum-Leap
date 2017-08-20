package cz.quantumleap.admin.menu;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.*;
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
        Map<RequestMappingInfo, HandlerMethod> handlerMethodMap = requestMappingHandlerMapping.getHandlerMethods();
        Map<String, MappingDefinitionAuthorize> map = new HashMap<>();

        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethodMap.entrySet()) {
            RequestMappingInfo requestMappingInfo = entry.getKey();
            HandlerMethod handlerMethod = entry.getValue();
            Method method = handlerMethod.getMethod();
            AdminMenuItemDefinition adminMenuItemDefinition = AnnotationUtils.findAnnotation(method, AdminMenuItemDefinition.class);
            if (adminMenuItemDefinition == null) {
                continue;
            }

            PreAuthorize preAuthorize = findPreAuthorize(handlerMethod);
            map.put(adminMenuItemDefinition.title(), new MappingDefinitionAuthorize(requestMappingInfo, adminMenuItemDefinition, preAuthorize));
        }

        for (MappingDefinitionAuthorize mappingDefinitionAuthorize : map.values()) {
            String parentByTitle = mappingDefinitionAuthorize.getParentByTitle();
            MappingDefinitionAuthorize parent = map.get(parentByTitle);
            if (parent != null) {
                mappingDefinitionAuthorize.isChild = true;
                parent.children.add(mappingDefinitionAuthorize);
            }
        }

        menuItems = map.values().stream()
                .filter(mappingDefinitionAuthorize -> !mappingDefinitionAuthorize.isChild)
                .map(MappingDefinitionAuthorize::toAdminMenuItem)
                .collect(Collectors.toList());
    }

    private PreAuthorize findPreAuthorize(HandlerMethod handlerMethod) {
        Class<?> beanType = handlerMethod.getBeanType();
        Method method = handlerMethod.getMethod();

        PreAuthorize methodPreAuthorize = AnnotationUtils.findAnnotation(method, PreAuthorize.class);
        PreAuthorize typePreAuthorize = AnnotationUtils.findAnnotation(beanType, PreAuthorize.class);

        return methodPreAuthorize != null ? methodPreAuthorize : typePreAuthorize;
    }

    private static class MappingDefinitionAuthorize {

        private final RequestMappingInfo requestMappingInfo;
        private final AdminMenuItemDefinition adminMenuItemDefinition;
        private final PreAuthorize preAuthorize;
        private final List<MappingDefinitionAuthorize> children = new ArrayList<>();
        private boolean isChild = false;

        private MappingDefinitionAuthorize(RequestMappingInfo requestMappingInfo, AdminMenuItemDefinition adminMenuItemDefinition, PreAuthorize preAuthorize) {
            this.requestMappingInfo = requestMappingInfo;
            this.adminMenuItemDefinition = adminMenuItemDefinition;
            this.preAuthorize = preAuthorize;
        }

        private String getParentByTitle() {
            return adminMenuItemDefinition.parentByTitle();
        }

        private AdminMenuItem toAdminMenuItem() {
            List<AdminMenuItem> childrenItems = children.stream().map(MappingDefinitionAuthorize::toAdminMenuItem).collect(Collectors.toList());
            return new AdminMenuItem(requestMappingInfo, adminMenuItemDefinition, preAuthorize, AdminMenuItem.State.NONE, childrenItems);
        }
    }
}
