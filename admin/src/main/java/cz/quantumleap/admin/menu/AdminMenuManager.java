package cz.quantumleap.admin.menu;

import com.google.common.collect.ComparisonChain;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class AdminMenuManager {

    private static final Comparator<MappingDefinitionAuthorize> MENU_ITEM_COMPARATOR = (o1, o2) -> ComparisonChain.start()
            .compare(o2.getPriority(), o1.getPriority())
            .compare(o1.getTitle(), o2.getTitle())
            .result();

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
        Map<RequestMappingInfo, HandlerMethod> handlerMethodMap = requestMappingHandlerMapping.getHandlerMethods();
        Map<String, MappingDefinitionAuthorize> map = new HashMap<>();

        handlerMethodMap.forEach((requestMappingInfo, handlerMethod) -> {
            Method method = handlerMethod.getMethod();
            AdminMenuItemDefinition adminMenuItemDefinition = AnnotationUtils.findAnnotation(method, AdminMenuItemDefinition.class);
            if (adminMenuItemDefinition == null) {
                return;
            }
            PreAuthorize preAuthorize = findPreAuthorize(handlerMethod);
            map.put(adminMenuItemDefinition.title(), new MappingDefinitionAuthorize(requestMappingInfo, adminMenuItemDefinition, preAuthorize));
        });

        handlerMethodMap.forEach((requestMappingInfo, handlerMethod) -> {
            Method method = handlerMethod.getMethod();
            AdminMenuItemActive adminMenuItemActive = AnnotationUtils.findAnnotation(method, AdminMenuItemActive.class);
            if (adminMenuItemActive == null) {
                return;
            }
            MappingDefinitionAuthorize mappingDefinitionAuthorize = map.get(adminMenuItemActive.value());
            if (mappingDefinitionAuthorize != null) {
                mappingDefinitionAuthorize.addRequestMappingInfo(requestMappingInfo);
            }
        });

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
                .sorted(MENU_ITEM_COMPARATOR)
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
            List<AdminMenuItem> childrenItems = children.stream().sorted(MENU_ITEM_COMPARATOR).map(MappingDefinitionAuthorize::toAdminMenuItem).collect(Collectors.toList());
            return new AdminMenuItem(requestMappingInfoList, adminMenuItemDefinition, preAuthorize, AdminMenuItem.State.NONE, childrenItems);
        }
    }
}
