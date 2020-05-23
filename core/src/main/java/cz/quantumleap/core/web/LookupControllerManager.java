package cz.quantumleap.core.web;

import cz.quantumleap.core.data.entity.EntityIdentifier;
import cz.quantumleap.core.data.transport.SliceRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Component
@ConditionalOnWebApplication
public class LookupControllerManager {

    private static final Logger log = LoggerFactory.getLogger(LookupControllerManager.class);

    private final ApplicationContext applicationContext;

    private final Map<EntityIdentifier<?>, LookupController> lookupControllerMap = new HashMap<>();

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    public LookupControllerManager(ApplicationContext applicationContext, RequestMappingHandlerMapping requestMappingHandlerMapping) {
        this.applicationContext = applicationContext;
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void initializeLookupControllerMap() {
        Map<String, LookupController> beans = applicationContext.getBeansOfType(LookupController.class);
        for (LookupController lookupController : beans.values()) {
            // If an authentication exception pops up at this place this means
            // that a controller's getEntityIdentifier() method is protected by
            // Spring Security and a security context is not accessible from
            // this place. Make sure the method is accessible.
            EntityIdentifier<?> entityIdentifier = lookupController.getLookupEntityIdentifier();
            if (entityIdentifier != null) {
                addController(entityIdentifier, lookupController);
            }
        }
    }

    public void registerController(LookupController lookupController) {
        registerController(lookupController.getLookupEntityIdentifier(), lookupController);
    }

    public void registerController(EntityIdentifier<?> entityIdentifier, LookupController lookupController) {
        if (!lookupControllerMap.containsValue(lookupController)) {
            RequestMappingInfo labelMapping = RequestMappingInfo.paths(lookupController.getLookupLabelUrl()).methods(RequestMethod.GET).build();
            Method resolveLookupLabelMethod = getLookupControllerMethod(lookupController.getClass(), "resolveLookupLabel", String.class, HttpServletRequest.class, HttpServletResponse.class);
            requestMappingHandlerMapping.registerMapping(labelMapping, lookupController, resolveLookupLabelMethod);

            RequestMappingInfo labelsMapping = RequestMappingInfo.paths(lookupController.getLookupLabelsUrl()).methods(RequestMethod.GET).build();
            Method findLookupLabelsMethod = getLookupControllerMethod(lookupController.getClass(), "findLookupLabels", String.class, Model.class, HttpServletRequest.class, HttpServletResponse.class);
            requestMappingHandlerMapping.registerMapping(labelsMapping, lookupController, findLookupLabelsMethod);

            RequestMappingInfo listMapping = RequestMappingInfo.paths(lookupController.getLookupListUrl()).methods(RequestMethod.GET).build();
            Method lookupListMethod = getLookupControllerMethod(lookupController.getClass(), "lookupList", SliceRequest.class, Model.class, HttpServletRequest.class, HttpServletResponse.class);
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

    private void addController(EntityIdentifier<?> entityIdentifier, LookupController lookupController) {
        LookupController registeredLookupController = lookupControllerMap.get(entityIdentifier);
        if (registeredLookupController == null) {
            lookupControllerMap.put(entityIdentifier, lookupController);
        } else {
            log.error("Two lookup controllers exists for an entity {}, first: {}, second: {}", entityIdentifier, registeredLookupController, lookupController);
        }
        lookupControllerMap.put(entityIdentifier, lookupController);

    }

    public LookupController getControllerForEntityIdentifier(String text) {
        return getControllerForEntityIdentifier(EntityIdentifier.parse(text));
    }

    public LookupController getControllerForEntityIdentifier(EntityIdentifier<?> entityIdentifier) {
        LookupController lookupController = lookupControllerMap.get(entityIdentifier);
        if (lookupController != null) {
            return lookupController;
        } else {
            throw new IllegalArgumentException("No accessible lookup controller with support of " + entityIdentifier + " was not found!");
        }
    }
}

