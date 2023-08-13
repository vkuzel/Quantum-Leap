package cz.quantumleap.core.view.controllerargument;

import cz.quantumleap.core.database.domain.FetchParams;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.MethodParameter;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static cz.quantumleap.core.database.domain.FetchParams.MAX_ITEMS;
import static java.util.Collections.emptyMap;

public class FetchParamsControllerArgumentResolver implements HandlerMethodArgumentResolver {

    public static final String SORT_PARAM_NAME = "sort";
    public static final String QUERY_PARAM_NAME = "query";
    public static final String OFFSET_PARAM_NAME = "offset";
    public static final String SIZE_PARAM_NAME = "size";
    private static final String QUALIFIER_DELIMITER = "_";

    private static final int DEFAULT_OFFSET = 0;

    private final SortHandlerMethodArgumentResolver sortHandlerMethodArgumentResolver;

    public FetchParamsControllerArgumentResolver(SortHandlerMethodArgumentResolver sortHandlerMethodArgumentResolver) {
        this.sortHandlerMethodArgumentResolver = sortHandlerMethodArgumentResolver;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return FetchParams.class.equals(parameter.getParameterType());
    }

    @Override
    public FetchParams resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        var qualifier = parameter.hasParameterAnnotation(Qualifier.class) ? parameter.getParameterAnnotation(Qualifier.class).value() : null;
        var query = getWebRequestStringParameter(webRequest, qualifier, QUERY_PARAM_NAME, null);
        var offset = getWebRequestIntParameter(webRequest, qualifier, OFFSET_PARAM_NAME, DEFAULT_OFFSET);
        var size = getWebRequestIntParameter(webRequest, qualifier, SIZE_PARAM_NAME, FetchParams.CHUNK_SIZE);

        if (offset >= MAX_ITEMS) {
            offset = MAX_ITEMS - 1;
        }
        if (offset + size > MAX_ITEMS) {
            size = MAX_ITEMS - offset;
        }

        var sort = sortHandlerMethodArgumentResolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);

        return new FetchParams(emptyMap(), query, null, offset, size, sort);
    }

    private int getWebRequestIntParameter(NativeWebRequest webRequest, String qualifier, String paramName, int defaultValue) {
        var qualifiedParamName = qualifyParamName(qualifier, paramName);
        var value = webRequest.getParameter(qualifiedParamName);
        return value != null ? Integer.parseInt(value) : defaultValue;
    }

    private String getWebRequestStringParameter(NativeWebRequest webRequest, String qualifier, String paramName, String defaultValue) {
        var qualifiedParamName = qualifyParamName(qualifier, paramName);
        var value = webRequest.getParameter(qualifiedParamName);
        return value != null ? value : defaultValue;
    }

    public static String qualifyParamName(String qualifier, String paramName) {
        return qualifier != null ? qualifier + QUALIFIER_DELIMITER + paramName : paramName;
    }
}
