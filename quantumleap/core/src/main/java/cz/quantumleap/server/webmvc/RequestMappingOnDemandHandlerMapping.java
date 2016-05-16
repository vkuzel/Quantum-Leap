package cz.quantumleap.server.webmvc;

import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.AbstractHandlerMethodMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

@Component
public class RequestMappingOnDemandHandlerMapping extends AbstractHandlerMethodMapping<RequestMappingGenerator> {

    private static final String LOOKUP_PATH_VARIABLE = "lookupPath";

    @Autowired
    ApplicationContext applicationContext;

    public RequestMappingOnDemandHandlerMapping() {
        setOrder(1);
    }

    @Override
    protected boolean isHandler(Class<?> beanType) {
        return ((AnnotationUtils.findAnnotation(beanType, Controller.class) != null) ||
                (AnnotationUtils.findAnnotation(beanType, RequestMappingOnDemand.class) != null));
    }

    @Override
    protected RequestMappingGenerator getMappingForMethod(Method method, Class<?> handlerType) {
        RequestMappingOnDemand requestMappingOnDemand = AnnotatedElementUtils.findMergedAnnotation(method, RequestMappingOnDemand.class);
        if (requestMappingOnDemand != null) {
            MappingGeneratorManager mappingGeneratorManager = applicationContext.getBean(requestMappingOnDemand.value());
            RequestMapping typeLevelRequestMapping = AnnotatedElementUtils.findMergedAnnotation(handlerType, RequestMapping.class);
            return mappingGeneratorManager.getMappingGenerator(typeLevelRequestMapping != null ? createRequestMappingInfo(typeLevelRequestMapping) : null);
        } else {
            return null;
        }
    }

    private RequestMappingInfo createRequestMappingInfo(RequestMapping requestMapping) {
        return RequestMappingInfo
                .paths(requestMapping.path())
                .methods(requestMapping.method())
                .params(requestMapping.params())
                .headers(requestMapping.headers())
                .consumes(requestMapping.consumes())
                .produces(requestMapping.produces())
                .mappingName(requestMapping.name())
                .build();
    }

    @Override
    protected Set<String> getMappingPathPatterns(RequestMappingGenerator generator) {
        return generator.getMappingPathPatterns();
    }

    @Override
    protected RequestMappingGenerator getMatchingMapping(RequestMappingGenerator generator, HttpServletRequest request) {
        return generator.getMatchingCondition(request);
    }

    @Override
    protected Comparator<RequestMappingGenerator> getMappingComparator(HttpServletRequest request) {
        return (generator1, generator2) -> generator1.compareTo(generator2, request);
    }

    @Override
    protected void handleMatch(RequestMappingGenerator generator, String lookupPath, HttpServletRequest request) {
        super.handleMatch(generator, lookupPath, request);

        String bestPattern;
        Map<String, String> decodedUriVariables;

        Set<String> patterns = generator.getMappingPathPatterns();
        if (patterns.isEmpty()) {
            bestPattern = lookupPath;
            decodedUriVariables = ImmutableMap.of(LOOKUP_PATH_VARIABLE, lookupPath);
        } else {
            bestPattern = patterns.iterator().next();
            Map<String, String> uriVariables = getPathMatcher().extractUriTemplateVariables(bestPattern, lookupPath);
            decodedUriVariables = getUrlPathHelper().decodePathVariables(request, uriVariables);
            decodedUriVariables.put(LOOKUP_PATH_VARIABLE, lookupPath);
        }

        request.setAttribute(BEST_MATCHING_PATTERN_ATTRIBUTE, bestPattern);
        request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, decodedUriVariables);

        if (!generator.getProducibleMediaTypes().isEmpty()) {
            Set<MediaType> mediaTypes = generator.getProducibleMediaTypes();
            request.setAttribute(PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE, mediaTypes);
        }
    }
}
