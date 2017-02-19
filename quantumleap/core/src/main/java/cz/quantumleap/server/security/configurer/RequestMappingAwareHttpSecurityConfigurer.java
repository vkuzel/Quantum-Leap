package cz.quantumleap.server.security.configurer;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import cz.quantumleap.server.security.annotation.LoginPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RequestMappingAwareHttpSecurityConfigurer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestMappingAwareHttpSecurityConfigurer.class);

    private static final Pattern SPEL_METHOD_ARGUMENT_PATTERN = Pattern.compile("/#[a-zA-Z]/");

    private static final List<Class<? extends Annotation>> UNSUPPORTED_ANNOTATIONS = ImmutableList.of(
            Secured.class, RolesAllowed.class, PermitAll.class, DenyAll.class
    );

    private final Map<RequestMappingInfo, HandlerMethod> handlerMethods;

    public RequestMappingAwareHttpSecurityConfigurer(Map<RequestMappingInfo, HandlerMethod> handlerMethods) {
        this.handlerMethods = handlerMethods;
    }

    public void configure(HttpSecurity httpSecurity) throws Exception {

        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = httpSecurity.authorizeRequests();

        Multimap<UrlSecurityMethod, String> urlSecurityMethodPatterns = findUrlSecurityMethodPatterns();
        registry = configureUrlMatcher(urlSecurityMethodPatterns, registry);

        registry = registry
                .antMatchers("/assets/**").permitAll()
                .anyRequest().authenticated();

        RequestMappingInfo loginMappingInfo = findLoginMappingInfo();
        if (loginMappingInfo != null) {
            configureLoginPage(loginMappingInfo, registry);
        }
    }

    private Multimap<UrlSecurityMethod, String> findUrlSecurityMethodPatterns() {

        Multimap<UrlSecurityMethod, String> urlSecurityMethodPatterns = ArrayListMultimap.create();

        for (RequestMappingInfo mappingInfo : handlerMethods.keySet()) {
            HandlerMethod handlerMethod = handlerMethods.get(mappingInfo);

            UrlSecurityConfigurer urlSecurityConfigurer = createUrlSecurityConfigurer(handlerMethod);
            if (urlSecurityConfigurer != null) {
                Set<UrlSecurityMethod> urlSecurityMethods = createUrlSecurityMethods(urlSecurityConfigurer, mappingInfo);
                urlSecurityMethods.forEach(urlSecurityMethod ->
                        urlSecurityMethodPatterns.putAll(urlSecurityMethod, mappingInfo.getPatternsCondition().getPatterns())
                );
            }
        }

        return urlSecurityMethodPatterns;
    }

    private RequestMappingInfo findLoginMappingInfo() {

        RequestMappingInfo loginMappingInfo = null;

        for (RequestMappingInfo mappingInfo : handlerMethods.keySet()) {
            HandlerMethod handlerMethod = handlerMethods.get(mappingInfo);

            LoginPage loginPageMethod = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), LoginPage.class);
            if (loginPageMethod != null && loginMappingInfo != null) {
                String msg = "Two RequestMappings with login page, " + loginMappingInfo + " and " + mappingInfo + " remove one of them!";
                throw new IllegalStateException(msg);
            } else if (loginPageMethod != null) {
                loginMappingInfo = mappingInfo;
            }
        }

        return loginMappingInfo;
    }

    ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry configureUrlMatcher(
            Multimap<UrlSecurityMethod, String> urlSecurityMethodPatterns,
            ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry
    ) {

        Map<UrlSecurityMethod, Collection<String>> urlSecurityMethodPatternsMap = urlSecurityMethodPatterns.asMap();
        for (UrlSecurityMethod urlSecurityMethod : urlSecurityMethodPatternsMap.keySet()) {

            String[] patterns = urlSecurityMethodPatternsMap.get(urlSecurityMethod).toArray(new String[]{});
            HttpMethod method = urlSecurityMethod.getHttpMethod();
            UrlSecurityConfigurer configurer = urlSecurityMethod.getUrlSecurityConfigurer();

            ExpressionUrlAuthorizationConfigurer<HttpSecurity>.MvcMatchersAuthorizedUrl authorizedUrl;
            if (method != null) {
                authorizedUrl = registry.mvcMatchers(method, patterns);
            } else {
                authorizedUrl = registry.mvcMatchers(patterns);
            }
            registry = configurer.configure(authorizedUrl);
        }

        return registry;
    }

    private void configureLoginPage(
            RequestMappingInfo loginMappingInfo,
            ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry
    ) throws Exception {

        Set<String> patterns = loginMappingInfo.getPatternsCondition().getPatterns();
        if (patterns.isEmpty()) {
            String msg = "No patterns in login page RequestMapping " + loginMappingInfo;
            throw new IllegalStateException(msg);
        } else if (patterns.size() > 1) {
            String msg = "Too many login page patterns in " + loginMappingInfo;
            throw new IllegalStateException(msg);
        }

        registry.and()
                .formLogin()
                .loginPage(patterns.iterator().next())
                .permitAll();
    }

    private Set<UrlSecurityMethod> createUrlSecurityMethods(UrlSecurityConfigurer configurer, RequestMappingInfo mappingInfo) {
        Set<HttpMethod> methods = getMappingInfoMethods(mappingInfo);
        if (methods.isEmpty()) {
            return Collections.singleton(new UrlSecurityMethod(configurer, null));
        } else {
            return methods.stream()
                    .map(method -> new UrlSecurityMethod(configurer, method))
                    .collect(Collectors.toSet());
        }
    }

    private Set<HttpMethod> getMappingInfoMethods(RequestMappingInfo mappingInfo) {
        Set<RequestMethod> methods = mappingInfo.getMethodsCondition().getMethods();
        return methods.stream()
                .map(method -> {
                    HttpMethod httpMethod = HttpMethod.resolve(method.name());
                    if (httpMethod == null) {
                        String msg = "Unknown method type " + method.name() + " for mapping " + mappingInfo;
                        throw new IllegalArgumentException(msg);
                    }
                    return httpMethod;
                })
                .collect(Collectors.toSet());
    }

    private UrlSecurityConfigurer createUrlSecurityConfigurer(HandlerMethod handlerMethod) {
        Class<?> beanType = handlerMethod.getBeanType();
        Method method = handlerMethod.getMethod();

        throwExceptionOnUnsupportedAnnotations(beanType, method);

        PreAuthorize methodPreAuthorize = AnnotationUtils.findAnnotation(method, PreAuthorize.class);
        PreAuthorize typePreAuthorize = AnnotationUtils.findAnnotation(beanType, PreAuthorize.class);

        methodPreAuthorize = filterSpELWithMethodArguments(methodPreAuthorize);
        typePreAuthorize = filterSpELWithMethodArguments(typePreAuthorize);

        if (methodPreAuthorize != null || typePreAuthorize != null) {
            return new UrlSecurityConfigurer(methodPreAuthorize, typePreAuthorize);
        }
        return null;
    }

    private void throwExceptionOnUnsupportedAnnotations(Class<?> beanType, Method method) {
        String msgPattern = "An unsupported %s annotation is applied on %s! Use the @PreAuthorize instead.";

        Annotation unsupportedMethodAnnotation = UNSUPPORTED_ANNOTATIONS.stream()
                .map(annotation -> AnnotationUtils.findAnnotation(method, annotation))
                .filter(Objects::nonNull)
                .findAny()
                .orElse(null);
        if (unsupportedMethodAnnotation != null) {
            String msg = String.format(msgPattern, unsupportedMethodAnnotation, method);
            throw new IllegalArgumentException(msg);
        }

        Annotation unsupportedTypeAnnotation = UNSUPPORTED_ANNOTATIONS.stream()
                .map(annotation -> AnnotationUtils.findAnnotation(beanType, annotation))
                .filter(Objects::nonNull)
                .findAny()
                .orElse(null);
        if (unsupportedTypeAnnotation != null) {
            String msg = String.format(msgPattern, unsupportedTypeAnnotation, beanType);
            throw new IllegalArgumentException(msg);
        }
    }

    private PreAuthorize filterSpELWithMethodArguments(PreAuthorize preAuthorize) {
        if (preAuthorize == null) {
            return null;
        }
        Matcher matcher = SPEL_METHOD_ARGUMENT_PATTERN.matcher(preAuthorize.value());
        if (matcher.find()) {
            LOGGER.warn("PreAuthorize annotation {} containing SpEL method arguments will not be included in HttpSecurity configuration!", preAuthorize);
            return null;
        }
        return preAuthorize;
    }

    static class UrlSecurityMethod {

        private final UrlSecurityConfigurer urlSecurityConfigurer;
        private final HttpMethod httpMethod;

        UrlSecurityMethod(UrlSecurityConfigurer urlSecurityConfigurer, HttpMethod httpMethod) {
            this.urlSecurityConfigurer = urlSecurityConfigurer;
            this.httpMethod = httpMethod;
        }

        UrlSecurityConfigurer getUrlSecurityConfigurer() {
            return urlSecurityConfigurer;
        }

        HttpMethod getHttpMethod() {
            return httpMethod;
        }
    }
}
