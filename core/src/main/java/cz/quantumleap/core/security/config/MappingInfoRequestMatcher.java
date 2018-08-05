package cz.quantumleap.core.security.config;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MappingInfoRequestMatcher implements RequestMatcher {

    private final List<RequestMappingInfo> mappingInfo;

    private MappingInfoRequestMatcher(List<RequestMappingInfo> mappingInfo) {
        this.mappingInfo = mappingInfo;
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        for (RequestMappingInfo requestMappingInfo : mappingInfo) {
            if (requestMappingInfo.getMatchingCondition(request) != null) {
                return true;
            }
        }
        return false;
    }

    public static class Builder {

        private static final String SPEL_PERMIT_ALL = "permitAll()";

        private final Map<RequestMappingInfo, HandlerMethod> handlerMethods;

        public Builder(Map<RequestMappingInfo, HandlerMethod> handlerMethods) {
            this.handlerMethods = handlerMethods;
        }

        public MappingInfoRequestMatcher build() {
            List<RequestMappingInfo> authenticatedMappingInfo = findRequestMappingInfo(this::hasNotPermitAll);
            return new MappingInfoRequestMatcher(authenticatedMappingInfo);
        }

        private List<RequestMappingInfo> findRequestMappingInfo(Predicate<HandlerMethod> handlerMethodPredicate) {
            return handlerMethods.entrySet().stream()
                    .filter(entry -> handlerMethodPredicate.test(entry.getValue()))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        }

        private boolean hasNotPermitAll(HandlerMethod handlerMethod) {
            Class<?> beanType = handlerMethod.getBeanType();
            Method method = handlerMethod.getMethod();

            PreAuthorize methodPreAuthorize = AnnotationUtils.findAnnotation(method, PreAuthorize.class);
            PreAuthorize typePreAuthorize = AnnotationUtils.findAnnotation(beanType, PreAuthorize.class);

            return !isPermitAll(methodPreAuthorize) && !isPermitAll(typePreAuthorize);
        }

        private boolean isPermitAll(PreAuthorize preAuthorize) {
            if (preAuthorize == null) {
                return false;
            }

            String spEl = preAuthorize.value();
            spEl = spEl.replace(" ", "");
            return spEl.contains(SPEL_PERMIT_ALL);
        }
    }
}
