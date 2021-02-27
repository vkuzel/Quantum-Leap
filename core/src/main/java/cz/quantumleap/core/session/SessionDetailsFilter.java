package cz.quantumleap.core.session;

import cz.quantumleap.core.view.WebUtils;
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
            session.setAttribute(SessionDao.REMOTE_ADDRESS_ATTRIBUTE, WebUtils.getRemoteAddr(request));
            session.setAttribute(SessionDao.USER_AGENT_ATTRIBUTE, request.getHeader("User-Agent"));
        }
    }
}
