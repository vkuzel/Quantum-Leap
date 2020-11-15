package cz.quantumleap.core.security.config;

import cz.quantumleap.core.security.IgnoreCsrfProtection;
import cz.quantumleap.core.web.WebUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class IgnoreCsrfProtectionRequestMatcher implements RequestMatcher {

    private final List<RequestMappingInfo> mappingInfo;

    private IgnoreCsrfProtectionRequestMatcher(List<RequestMappingInfo> mappingInfo) {
        this.mappingInfo = mappingInfo;
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        WebUtils.cacheRequestPath(request);
        for (RequestMappingInfo requestMappingInfo : mappingInfo) {
            if (requestMappingInfo.getMatchingCondition(request) != null) {
                return true;
            }
        }
        return false;
    }

    public static class Builder {

        private final Map<RequestMappingInfo, HandlerMethod> handlerMethods;

        public Builder(Map<RequestMappingInfo, HandlerMethod> handlerMethods) {
            this.handlerMethods = handlerMethods;
        }

        public IgnoreCsrfProtectionRequestMatcher build() {
            List<RequestMappingInfo> ignoreCsrfProtectionMappingInfo = findRequestMappingInfo(this::hasIgnoreCsrfProtection);
            return new IgnoreCsrfProtectionRequestMatcher(ignoreCsrfProtectionMappingInfo);
        }

        private List<RequestMappingInfo> findRequestMappingInfo(Predicate<HandlerMethod> handlerMethodPredicate) {
            return handlerMethods.entrySet().stream()
                    .filter(entry -> handlerMethodPredicate.test(entry.getValue()))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        }

        private boolean hasIgnoreCsrfProtection(HandlerMethod handlerMethod) {
            Class<?> beanType = handlerMethod.getBeanType();
            Method method = handlerMethod.getMethod();

            IgnoreCsrfProtection methodIgnoreCsrfProtection = AnnotationUtils.findAnnotation(method, IgnoreCsrfProtection.class);
            IgnoreCsrfProtection typeIgnoreCsrfProtection = AnnotationUtils.findAnnotation(beanType, IgnoreCsrfProtection.class);

            return methodIgnoreCsrfProtection != null || typeIgnoreCsrfProtection != null;
        }
    }
}
