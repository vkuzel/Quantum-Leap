package cz.quantumleap.core.web.controllerargument;

import cz.quantumleap.core.data.transport.SliceRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.HashMap;

import static cz.quantumleap.core.data.transport.SliceRequest.MAX_ITEMS;

public class SliceRequestControllerArgumentResolver implements HandlerMethodArgumentResolver {

    public static final String SORT_PARAM_NAME = "sort";
    public static final String QUERY_PARAM_NAME = "query";
    public static final String OFFSET_PARAM_NAME = "offset";
    public static final String SIZE_PARAM_NAME = "size";
    private static final String QUALIFIER_DELIMITER = "_";

    private static final int DEFAULT_OFFSET = 0;

    private final SortHandlerMethodArgumentResolver sortHandlerMethodArgumentResolver;

    public SliceRequestControllerArgumentResolver(SortHandlerMethodArgumentResolver sortHandlerMethodArgumentResolver) {
        this.sortHandlerMethodArgumentResolver = sortHandlerMethodArgumentResolver;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return SliceRequest.class.equals(parameter.getParameterType());
    }

    @Override
    public SliceRequest resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String qualifier = parameter != null && parameter.hasParameterAnnotation(Qualifier.class) ? parameter.getParameterAnnotation(Qualifier.class).value() : null;
        String query = getWebRequestStringParameter(webRequest, qualifier, QUERY_PARAM_NAME, null);
        int offset = getWebRequestIntParameter(webRequest, qualifier, OFFSET_PARAM_NAME, DEFAULT_OFFSET);
        int size = getWebRequestIntParameter(webRequest, qualifier, SIZE_PARAM_NAME, SliceRequest.CHUNK_SIZE);

        if (offset >= MAX_ITEMS) {
            offset = MAX_ITEMS - 1;
        }
        if (offset + size > MAX_ITEMS) {
            size = MAX_ITEMS - offset;
        }

        Sort sort = sortHandlerMethodArgumentResolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);

        return new SliceRequest(new HashMap<>(), query, offset, size, sort, null);
    }

    private int getWebRequestIntParameter(NativeWebRequest webRequest, String qualifier, String paramName, int defaultValue) {
        String qualifiedParamName = qualifyParamName(qualifier, paramName);
        String value = webRequest.getParameter(qualifiedParamName);
        return value != null ? Integer.parseInt(value) : defaultValue;
    }

    private String getWebRequestStringParameter(NativeWebRequest webRequest, String qualifier, String paramName, String defaultValue) {
        String qualifiedParamName = qualifyParamName(qualifier, paramName);
        String value = webRequest.getParameter(qualifiedParamName);
        return value != null ? value : defaultValue;
    }

    public static String qualifyParamName(String qualifier, String paramName) {
        return qualifier != null ? qualifier + QUALIFIER_DELIMITER + paramName : paramName;
    }
}
