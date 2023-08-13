package cz.quantumleap.core.view;

import cz.quantumleap.core.business.LookupService;
import cz.quantumleap.core.database.domain.FetchParams;
import cz.quantumleap.core.database.entity.EntityIdentifier;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static cz.quantumleap.core.view.WebUtils.requestMappingInfoBuilder;

@Component
@ConditionalOnWebApplication
public class LookupRegistry {

    private static final Logger log = LoggerFactory.getLogger(LookupRegistry.class);

    private final ApplicationContext applicationContext;

    private final Map<EntityIdentifier<?>, LookupService> lookupServiceMap = new HashMap<>();
    private final Map<EntityIdentifier<?>, LookupController> lookupControllerMap = new HashMap<>();

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    public LookupRegistry(ApplicationContext applicationContext, RequestMappingHandlerMapping requestMappingHandlerMapping) {
        this.applicationContext = applicationContext;
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    }

    @SuppressWarnings("unused")
    public LookupController getControllerForEntityIdentifier(String entityIdentifierCode) {
        var entityIdentifier = EntityIdentifier.parse(entityIdentifierCode);
        return getControllerForEntityIdentifier(entityIdentifier);
    }

    public LookupController getControllerForEntityIdentifier(EntityIdentifier<?> entityIdentifier) {
        var lookupController = lookupControllerMap.get(entityIdentifier);
        if (lookupController != null) {
            return lookupController;
        } else {
            var msg = "No lookup controller for entity identifier %s was found!";
            throw new IllegalArgumentException(String.format(msg, entityIdentifier));
        }
    }

    @SuppressWarnings("unused")
    public String getLabel(String entityIdentifierCode, Object entityId) {
        var entityIdentifier = EntityIdentifier.parse(entityIdentifierCode);
        return getLabel(entityIdentifier, entityId);
    }

    public String getLabel(EntityIdentifier<?> entityIdentifier, Object entityId) {
        var lookupService = lookupServiceMap.get(entityIdentifier);
        if (lookupService != null) {
            return lookupService.findLookupLabel(entityId);
        } else {
            var msg = "No lookup service for entity identifier %s was found!";
            throw new IllegalArgumentException(String.format(msg, entityIdentifier));
        }
    }

    /**
     * Allow to register non-Spring bean lookup controllers manually.
     */
    public void registerController(LookupController lookupController) {
        registerController(lookupController.getLookupEntityIdentifier(), lookupController);
    }

    public void registerController(EntityIdentifier<?> entityIdentifier, LookupController lookupController) {
        if (!lookupControllerMap.containsValue(lookupController)) {
            var labelMapping = requestMappingInfoBuilder(lookupController.getLookupLabelUrl())
                    .methods(RequestMethod.GET)
                    .build();
            var resolveLookupLabelMethod = getLookupControllerMethod(
                    lookupController.getClass(),
                    "resolveLookupLabel",
                    String.class,
                    HttpServletRequest.class,
                    HttpServletResponse.class
            );
            requestMappingHandlerMapping.registerMapping(labelMapping, lookupController, resolveLookupLabelMethod);

            var labelsMapping = requestMappingInfoBuilder(lookupController.getLookupLabelsUrl())
                    .methods(RequestMethod.GET)
                    .build();
            var findLookupLabelsMethod = getLookupControllerMethod(
                    lookupController.getClass(),
                    "findLookupLabels",
                    String.class,
                    Model.class,
                    HttpServletRequest.class,
                    HttpServletResponse.class
            );
            requestMappingHandlerMapping.registerMapping(labelsMapping, lookupController, findLookupLabelsMethod);

            var listMapping = requestMappingInfoBuilder(lookupController.getLookupListUrl())
                    .methods(RequestMethod.GET)
                    .build();
            var lookupListMethod = getLookupControllerMethod(
                    lookupController.getClass(),
                    "lookupList",
                    FetchParams.class,
                    Model.class,
                    HttpServletRequest.class,
                    HttpServletResponse.class
            );
            requestMappingHandlerMapping.registerMapping(listMapping, lookupController, lookupListMethod);
        }

        addController(entityIdentifier, lookupController);
    }

    private Method getLookupControllerMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        try {
            return clazz.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    @EventListener(ContextRefreshedEvent.class)
    public void initializeLookupMaps() {
        var lookupServiceMap = applicationContext.getBeansOfType(LookupService.class);
        for (var lookupService : lookupServiceMap.values()) {
            var entityIdentifier = lookupService.getLookupEntityIdentifier(null);
            if (entityIdentifier != null) {
                addService(entityIdentifier, lookupService);
            }
        }

        var lookupControllerMap = applicationContext.getBeansOfType(LookupController.class);
        for (var lookupController : lookupControllerMap.values()) {
            // If an authentication exception pops up at this place this means
            // that a controller's getEntityIdentifier() method is protected by
            // Spring Security and a security context is not accessible from
            // this place. Make sure the method is accessible.
            var entityIdentifier = lookupController.getLookupEntityIdentifier();
            if (entityIdentifier != null) {
                addController(entityIdentifier, lookupController);
            }
        }
    }

    private void addService(EntityIdentifier<?> entityIdentifier, LookupService lookupService) {
        var registeredLookupService = lookupServiceMap.get(entityIdentifier);
        if (registeredLookupService == null) {
            lookupServiceMap.put(entityIdentifier, lookupService);
        } else {
            var msg = "Two lookup services exists for an entity identifier {}, first: {}, second: {}";
            log.error(msg, entityIdentifier, registeredLookupService, lookupService);
        }
    }

    private void addController(EntityIdentifier<?> entityIdentifier, LookupController lookupController) {
        var registeredLookupController = lookupControllerMap.get(entityIdentifier);
        if (registeredLookupController == null) {
            lookupControllerMap.put(entityIdentifier, lookupController);
        } else {
            var msg = "Two lookup controllers exists for an entity identifier {}, first: {}, second: {}";
            log.error(msg, entityIdentifier, registeredLookupController, lookupController);
        }
    }
}

