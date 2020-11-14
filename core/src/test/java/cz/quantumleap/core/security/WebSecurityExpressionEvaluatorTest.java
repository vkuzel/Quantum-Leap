package cz.quantumleap.core.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

import static org.mockito.Mockito.doReturn;

public class WebSecurityExpressionEvaluatorTest {

    private HttpServletRequest httpServletRequest;
    private HttpServletResponse httpServletResponse;

    @BeforeEach
    public void mockBeans() {
        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        httpServletResponse = Mockito.mock(HttpServletResponse.class);
    }

    @Test
    public void filterInvocationSecrityHandlerEvaluatesExpression() {
        // given
        Expression expression = Mockito.mock(Expression.class);
        EvaluationContext evaluationContext = Mockito.mock(EvaluationContext.class);
        doReturn(true).when(expression).getValue(evaluationContext, Boolean.class);
        ExpressionParser expressionParser = Mockito.mock(ExpressionParser.class);
        doReturn(expression).when(expressionParser).parseExpression("expression");
        TestSecurityExpressionHandler testSecurityExpressionHandler = new TestSecurityExpressionHandler(expressionParser, evaluationContext);
        WebSecurityExpressionEvaluator evaluator = new WebSecurityExpressionEvaluator(Collections.singletonList(testSecurityExpressionHandler));

        // when
        boolean result = evaluator.evaluate("expression", httpServletRequest, httpServletResponse);

        // then
        Assertions.assertTrue(result);
    }

    private static class TestSecurityExpressionHandler implements SecurityExpressionHandler<FilterInvocation> {

        private final ExpressionParser expressionParser;
        private final EvaluationContext evaluationContext;

        private TestSecurityExpressionHandler(ExpressionParser expressionParser, EvaluationContext evaluationContext) {
            this.expressionParser = expressionParser;
            this.evaluationContext = evaluationContext;
        }

        @Override
        public ExpressionParser getExpressionParser() {
            return expressionParser;
        }

        @Override
        public EvaluationContext createEvaluationContext(Authentication authentication, FilterInvocation invocation) {
            return evaluationContext;
        }
    }
}
