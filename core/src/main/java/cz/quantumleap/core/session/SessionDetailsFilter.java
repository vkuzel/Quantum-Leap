package cz.quantumleap.core.session;

import cz.quantumleap.core.view.WebUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 101)
public class SessionDetailsFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        filterChain.doFilter(request, response);

        var session = request.getSession(false);
        if (session != null) {
            session.setAttribute(SessionDao.REMOTE_ADDRESS_ATTRIBUTE, WebUtils.getRemoteAddr(request));
            session.setAttribute(SessionDao.USER_AGENT_ATTRIBUTE, request.getHeader("User-Agent"));
        }
    }
}
