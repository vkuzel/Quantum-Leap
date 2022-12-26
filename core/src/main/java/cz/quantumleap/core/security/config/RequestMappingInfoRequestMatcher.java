package cz.quantumleap.core.security.config;

import cz.quantumleap.core.view.WebUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.List;

public class RequestMappingInfoRequestMatcher implements RequestMatcher {

    private final List<RequestMappingInfo> mappingInfo;

    public RequestMappingInfoRequestMatcher(List<RequestMappingInfo> mappingInfo) {
        this.mappingInfo = mappingInfo;
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        if (WebUtils.isDummyRequest(request)) {
            return false;
        }

        WebUtils.cacheRequestPath(request);
        for (RequestMappingInfo requestMappingInfo : mappingInfo) {
            if (requestMappingInfo.getMatchingCondition(request) != null) {
                return true;
            }
        }
        return false;
    }
}
