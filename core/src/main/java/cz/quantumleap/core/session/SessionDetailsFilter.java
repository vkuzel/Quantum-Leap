package cz.quantumleap.core.session;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 101)
public class SessionDetailsFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        filterChain.doFilter(request, response);

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.setAttribute(SessionDao.REMOTE_ADDRESS_ATTRIBUTE, getRemoteAddr(request));
            session.setAttribute(SessionDao.USER_AGENT_ATTRIBUTE, request.getHeader("User-Agent"));
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
}
