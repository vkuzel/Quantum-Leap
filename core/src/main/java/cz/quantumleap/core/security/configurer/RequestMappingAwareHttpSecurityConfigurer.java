package cz.quantumleap.core.security.configurer;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RequestMappingAwareHttpSecurityConfigurer {

    private static final String[] STATIC_CONTENT_ENDPOINTS = new String[]{
            "/webjars/**", "/assets/**"
    };
    private static final String SPEL_PERMIT_ALL = "permitAll()";

    private final Map<RequestMappingInfo, HandlerMethod> handlerMethods;

    public RequestMappingAwareHttpSecurityConfigurer(Map<RequestMappingInfo, HandlerMethod> handlerMethods) {
        this.handlerMethods = handlerMethods;
    }

    public void configure(HttpSecurity httpSecurity) throws Exception {
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = httpSecurity.authorizeRequests();
        List<RequestMappingInfo> permitAllMappingInfo = findRequestMappingInfo(this::hasPermitAll);
        registry
                .requestMatchers(new MappingInfoRequestMatcher(permitAllMappingInfo)).permitAll()
                .antMatchers(STATIC_CONTENT_ENDPOINTS).permitAll()
                .anyRequest().authenticated();
    }

    private List<RequestMappingInfo> findRequestMappingInfo(Predicate<HandlerMethod> handlerMethodPredicate) {
        return handlerMethods.entrySet().stream()
                .filter(entry -> handlerMethodPredicate.test(entry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private boolean hasPermitAll(HandlerMethod handlerMethod) {
        Class<?> beanType = handlerMethod.getBeanType();
        Method method = handlerMethod.getMethod();

        PreAuthorize methodPreAuthorize = AnnotationUtils.findAnnotation(method, PreAuthorize.class);
        PreAuthorize typePreAuthorize = AnnotationUtils.findAnnotation(beanType, PreAuthorize.class);

        return methodPreAuthorize != null && isPermitAll(methodPreAuthorize)
                || methodPreAuthorize == null && typePreAuthorize != null && isPermitAll(typePreAuthorize);
    }

    private boolean isPermitAll(PreAuthorize preAuthorize) {
        String spEl = preAuthorize.value();
        spEl = spEl.replace(" ", "");
        return spEl.contains(SPEL_PERMIT_ALL);
    }

    private static class MappingInfoRequestMatcher implements RequestMatcher {

        private final List<RequestMappingInfo> mappingInfo;

        public MappingInfoRequestMatcher(List<RequestMappingInfo> mappingInfo) {
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
    }
}
