package cz.quantumleap.core.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.GenericTypeResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.security.access.expression.ExpressionUtils;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
        SecurityExpressionHandler<FilterInvocation> handler = getFilterSecurityHandler();
        Expression expression = handler.getExpressionParser().parseExpression(securityExpression);
        EvaluationContext evaluationContext = createEvaluationContext(handler, request, response);
        return ExpressionUtils.evaluateAsBoolean(expression, evaluationContext);
    }

    private EvaluationContext createEvaluationContext(SecurityExpressionHandler<FilterInvocation> handler, HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        FilterInvocation filterInvocation = new FilterInvocation(request, response, EMPTY_CHAIN);

        return handler.createEvaluationContext(authentication, filterInvocation);
    }

    @SuppressWarnings("unchecked")
    private SecurityExpressionHandler<FilterInvocation> getFilterSecurityHandler() {
        for (SecurityExpressionHandler<?> handler : securityExpressionHandlers) {
            if (FilterInvocation.class.equals(GenericTypeResolver.resolveTypeArgument(handler.getClass(), SecurityExpressionHandler.class))) {
                return (SecurityExpressionHandler<FilterInvocation>) handler;
            }
        }
        throw new IllegalStateException("No filter invocation security expression handler has been found! Handlers: " + securityExpressionHandlers.size());
    }
}
