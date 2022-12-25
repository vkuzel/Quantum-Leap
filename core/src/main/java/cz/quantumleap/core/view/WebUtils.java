package cz.quantumleap.core.view;

import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo.BuilderConfiguration;
import org.springframework.web.util.ServletRequestPathUtils;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.pattern.PathPatternParser;

import javax.servlet.http.HttpServletRequest;

public class WebUtils {

    public static boolean isDummyRequest(HttpServletRequest request) {
        String name = request.getClass().getName();
        return "org.springframework.security.web.FilterInvocation$DummyRequest".equals(name);
    }

    public static void cacheRequestPath(HttpServletRequest request) {
        // In https://github.com/spring-projects/spring-framework/issues/24945
        // Spring 5.3 introduced path pattern parses which caches parsed result
        // into a request's attribute ServletRequestPathUtils.PATH_ATTRIBUTE.
        // Request mapping info then expects parsed pattern to be already
        // cached. If it is not so it will throw an exception, so we have to
        // perform parsing manually.
        if (!ServletRequestPathUtils.hasParsedRequestPath(request)) {
            ServletRequestPathUtils.parseAndCache(request);
        }
        // Similar case to previous introduced in same task. Same pattern of
        // checking whether attribute exists and then caching it can be found
        // in Spring's code.
        if (request.getAttribute(UrlPathHelper.PATH_ATTRIBUTE) == null) {
            UrlPathHelper.defaultInstance.resolveAndCacheLookupPath(request);
        }
    }

    public static String getRemoteAddr(HttpServletRequest request) {
        // In future there should be a support for RFC 7239 Forwarded header.
        // Unfortunately at the moment nginx does not have built-in support
        // fot the new header so legacy X-Forwarded-For is used.
        String remoteAddr = request.getHeader("X-FORWARDED-FOR");
        if (remoteAddr == null) {
            remoteAddr = request.getRemoteAddr();
        } else if (remoteAddr.contains(",")) {
            remoteAddr = remoteAddr.split(",")[0];
        }
        return remoteAddr;
    }

    /**
     * Request mapping info builder with PathPatternMatcher.
     */
    public static RequestMappingInfo.Builder requestMappingInfoBuilder(String path) {
        BuilderConfiguration options = new BuilderConfiguration();
        options.setPatternParser(new PathPatternParser());
        return RequestMappingInfo.paths(path).options(options);
    }
}
