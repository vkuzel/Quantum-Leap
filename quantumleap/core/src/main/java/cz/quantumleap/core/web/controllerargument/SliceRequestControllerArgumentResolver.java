package cz.quantumleap.core.web.controllerargument;

import cz.quantumleap.core.data.transport.SliceRequest;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static cz.quantumleap.core.data.transport.SliceRequest.MAX_ITEMS;

public class SliceRequestControllerArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String FILTER_PARAM_NAME = "filter";
    private static final String OFFSET_PARAM_NAME = "offset";
    private static final String SIZE_PARAM_NAME = "size";

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

        int offset = getWebRequestIntParameter(webRequest, OFFSET_PARAM_NAME, DEFAULT_OFFSET);
        int size = getWebRequestIntParameter(webRequest, SIZE_PARAM_NAME, SliceRequest.CHUNK_SIZE);

        if (offset >= MAX_ITEMS) {
            offset = MAX_ITEMS - 1;
        }
        if (offset + size > MAX_ITEMS) {
            size = MAX_ITEMS - offset;
        }

        Sort sort = sortHandlerMethodArgumentResolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);

        return new SliceRequest(offset, size, sort);
    }

    private int getWebRequestIntParameter(NativeWebRequest webRequest, String paramName, int defaultValue) {
        String value = webRequest.getParameter(paramName);
        return value != null ? Integer.parseInt(value) : defaultValue;
    }
}
