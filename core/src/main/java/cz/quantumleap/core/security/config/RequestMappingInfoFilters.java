package cz.quantumleap.core.security.config;

import cz.quantumleap.core.security.IgnoreCsrfProtection;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.Map.Entry;

import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

public class RequestMappingInfoFilters {

    private static final String SPEL_PERMIT_ALL = "permitAll()";

    public static boolean hasPermitAll(Entry<RequestMappingInfo, HandlerMethod> requestMappingInfoEntry) {
        var handlerMethod = requestMappingInfoEntry.getValue();
        var beanType = handlerMethod.getBeanType();
        var method = handlerMethod.getMethod();

        var methodPreAuthorize = findAnnotation(method, PreAuthorize.class);
        var typePreAuthorize = findAnnotation(beanType, PreAuthorize.class);

        return containsPermitAllSpel(methodPreAuthorize) || containsPermitAllSpel(typePreAuthorize);
    }

    private static boolean containsPermitAllSpel(PreAuthorize preAuthorize) {
        if (preAuthorize == null) {
            return false;
        }

        var spEl = preAuthorize.value();
        spEl = spEl.replace(" ", "");
        return spEl.contains(SPEL_PERMIT_ALL);
    }

    public static boolean hasIgnoreCsrf(Entry<RequestMappingInfo, HandlerMethod> requestMappingInfoEntry) {
        var handlerMethod = requestMappingInfoEntry.getValue();
        var beanType = handlerMethod.getBeanType();
        var method = handlerMethod.getMethod();

        var methodIgnoreCsrfProtection = AnnotationUtils.findAnnotation(method, IgnoreCsrfProtection.class);
        var typeIgnoreCsrfProtection = AnnotationUtils.findAnnotation(beanType, IgnoreCsrfProtection.class);

        return methodIgnoreCsrfProtection != null || typeIgnoreCsrfProtection != null;
    }
}
