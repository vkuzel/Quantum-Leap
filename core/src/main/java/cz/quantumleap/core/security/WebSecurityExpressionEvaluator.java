package cz.quantumleap.core.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.GenericTypeResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.security.access.expression.ExpressionUtils;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Component;

import java.util.List;

// Based on org.thymeleaf.extras.springsecurity4.auth.AuthUtils
@Component
@ConditionalOnWebApplication
public class WebSecurityExpressionEvaluator {

    private static final FilterChain EMPTY_CHAIN = (request, response) -> {
        throw new UnsupportedOperationException();
    };

    private final List<SecurityExpressionHandler<?>> securityExpressionHandlers;

    public WebSecurityExpressionEvaluator(List<SecurityExpressionHandler<?>> securityExpressionHandlers) {
        this.securityExpressionHandlers = securityExpressionHandlers;
    }

    public boolean evaluate(String securityExpression, HttpServletRequest request, HttpServletResponse response) {
        var handler = getFilterSecurityHandler();
        var expression = handler.getExpressionParser().parseExpression(securityExpression);
        var evaluationContext = createEvaluationContext(handler, request, response);
        return ExpressionUtils.evaluateAsBoolean(expression, evaluationContext);
    }

    private EvaluationContext createEvaluationContext(SecurityExpressionHandler<FilterInvocation> handler, HttpServletRequest request, HttpServletResponse response) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var filterInvocation = new FilterInvocation(request, response, EMPTY_CHAIN);

        return handler.createEvaluationContext(authentication, filterInvocation);
    }

    @SuppressWarnings("unchecked")
    private SecurityExpressionHandler<FilterInvocation> getFilterSecurityHandler() {
        for (var handler : securityExpressionHandlers) {
            if (FilterInvocation.class.equals(GenericTypeResolver.resolveTypeArgument(handler.getClass(), SecurityExpressionHandler.class))) {
                return (SecurityExpressionHandler<FilterInvocation>) handler;
            }
        }
        throw new IllegalStateException("No filter invocation security expression handler has been found! Handlers: " + securityExpressionHandlers.size());
    }
}
