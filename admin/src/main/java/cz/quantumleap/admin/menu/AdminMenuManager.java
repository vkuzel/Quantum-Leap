package cz.quantumleap.admin.menu;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class AdminMenuManager {

    private static final Comparator<MappingDefinitionAuthorize> MENU_ITEM_COMPARATOR = Comparator
            .comparingInt(MappingDefinitionAuthorize::getPriority).reversed()
            .thenComparing(MappingDefinitionAuthorize::getTitle);

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    private List<AdminMenuItem> menuItems = Collections.emptyList();

    public AdminMenuManager(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    }

    public List<AdminMenuItem> getMenuItems() {
        return menuItems;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void buildMenu() {
        var handlerMethodMap = requestMappingHandlerMapping.getHandlerMethods();
        Map<String, MappingDefinitionAuthorize> map = new HashMap<>();

        handlerMethodMap.forEach((requestMappingInfo, handlerMethod) -> {
            var method = handlerMethod.getMethod();
            var adminMenuItemDefinition = AnnotationUtils.findAnnotation(method, AdminMenuItemDefinition.class);
            if (adminMenuItemDefinition == null) {
                return;
            }
            var preAuthorize = findPreAuthorize(handlerMethod);
            map.put(adminMenuItemDefinition.title(), new MappingDefinitionAuthorize(requestMappingInfo, adminMenuItemDefinition, preAuthorize));
        });

        handlerMethodMap.forEach((requestMappingInfo, handlerMethod) -> {
            var method = handlerMethod.getMethod();
            var adminMenuItemActive = AnnotationUtils.findAnnotation(method, AdminMenuItemActive.class);
            if (adminMenuItemActive == null) {
                return;
            }
            var mappingDefinitionAuthorize = map.get(adminMenuItemActive.value());
            if (mappingDefinitionAuthorize != null) {
                mappingDefinitionAuthorize.addRequestMappingInfo(requestMappingInfo);
            }
        });

        for (var mappingDefinitionAuthorize : map.values()) {
            var parentByTitle = mappingDefinitionAuthorize.getParentByTitle();
            var parent = map.get(parentByTitle);
            if (parent != null) {
                mappingDefinitionAuthorize.isChild = true;
                parent.children.add(mappingDefinitionAuthorize);
            }
        }

        menuItems = map.values().stream()
                .filter(mappingDefinitionAuthorize -> !mappingDefinitionAuthorize.isChild)
                .sorted(MENU_ITEM_COMPARATOR)
                .map(MappingDefinitionAuthorize::toAdminMenuItem)
                .collect(Collectors.toList());
    }

    private PreAuthorize findPreAuthorize(HandlerMethod handlerMethod) {
        var beanType = handlerMethod.getBeanType();
        var method = handlerMethod.getMethod();

        var methodPreAuthorize = AnnotationUtils.findAnnotation(method, PreAuthorize.class);
        var typePreAuthorize = AnnotationUtils.findAnnotation(beanType, PreAuthorize.class);

        return methodPreAuthorize != null ? methodPreAuthorize : typePreAuthorize;
    }

    private static class MappingDefinitionAuthorize {

        private final List<RequestMappingInfo> requestMappingInfoList = new ArrayList<>();
        private final AdminMenuItemDefinition adminMenuItemDefinition;
        private final PreAuthorize preAuthorize;
        private final List<MappingDefinitionAuthorize> children = new ArrayList<>();
        private boolean isChild = false;

        private MappingDefinitionAuthorize(RequestMappingInfo requestMappingInfo, AdminMenuItemDefinition adminMenuItemDefinition, PreAuthorize preAuthorize) {
            this.requestMappingInfoList.add(requestMappingInfo);
            this.adminMenuItemDefinition = adminMenuItemDefinition;
            this.preAuthorize = preAuthorize;
        }

        private void addRequestMappingInfo(RequestMappingInfo requestMappingInfo) {
            requestMappingInfoList.add(requestMappingInfo);
        }

        private String getParentByTitle() {
            return adminMenuItemDefinition.parentByTitle();
        }

        private String getTitle() {
            return adminMenuItemDefinition.title();
        }

        private int getPriority() {
            return adminMenuItemDefinition.priority();
        }

        private AdminMenuItem toAdminMenuItem() {
            var childrenItems = children.stream().sorted(MENU_ITEM_COMPARATOR).map(MappingDefinitionAuthorize::toAdminMenuItem).collect(Collectors.toList());
            return new AdminMenuItem(requestMappingInfoList, adminMenuItemDefinition, preAuthorize, AdminMenuItem.State.NONE, childrenItems);
        }
    }
}
