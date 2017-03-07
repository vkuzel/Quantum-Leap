package cz.quantumleap.core.security.configurer;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.Validate;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RequestMappingAwareHttpSecurityConfigurer {

    private static final String[] STATIC_CONTENT_ENDPOINTS = new String[] {
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

        Multimap<HttpMethod, String> patternsByMethod = groupPatternsByHttpMethod(permitAllMappingInfo);
        patternsByMethod.asMap().forEach((method, patterns) ->
                registry.mvcMatchers(method, patterns.toArray(new String[]{})).permitAll()
        );

        List<String> patterns = getPatternsWithoutHttpMethod(permitAllMappingInfo);
        registry.mvcMatchers(patterns.toArray(new String[]{})).permitAll();

        registry
                .antMatchers(STATIC_CONTENT_ENDPOINTS).permitAll()
                .anyRequest().authenticated();
    }

    private Multimap<HttpMethod, String> groupPatternsByHttpMethod(List<RequestMappingInfo> requestMappingInfo) {

        Multimap<HttpMethod, String> patternsByMethod = ArrayListMultimap.create();

        for (RequestMappingInfo mappingInfo : requestMappingInfo) {
            Set<RequestMethod> methods = mappingInfo.getMethodsCondition().getMethods();

            methods.forEach(method -> {
                HttpMethod httpMethod = HttpMethod.resolve(method.name());
                Validate.notNull(httpMethod, "Unknown method %s", method);
                patternsByMethod.putAll(httpMethod, mappingInfo.getPatternsCondition().getPatterns());
            });
        }

        return patternsByMethod;
    }

    private List<String> getPatternsWithoutHttpMethod(List<RequestMappingInfo> requestMappingInfo) {
        return requestMappingInfo.stream()
                .filter(rmi -> rmi.getMethodsCondition().getMethods() == null || rmi.getMethodsCondition().getMethods().isEmpty())
                .flatMap(rmi -> rmi.getPatternsCondition().getPatterns().stream())
                .collect(Collectors.toList());
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
}
