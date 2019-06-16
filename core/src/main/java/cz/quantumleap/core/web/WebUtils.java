package cz.quantumleap.core.web;

import javax.servlet.http.HttpServletRequest;

public class WebUtils {

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
}
