package cz.quantumleap.core.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class WebSecurityExpressionEvaluatorTest {

    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private HttpServletResponse httpServletResponse;
    @Mock
    private ExpressionParser expressionParser;
    @Mock
    private Expression expression;
    @Mock
    private EvaluationContext evaluationContext;
    private SecurityExpressionHandler<FilterInvocation> securityExpressionHandler = new SecurityExpressionHandler<FilterInvocation>() {
        @Override
        public ExpressionParser getExpressionParser() {
            return expressionParser;
        }

        @Override
        public EvaluationContext createEvaluationContext(Authentication authentication, FilterInvocation invocation) {
            return evaluationContext;
        }
    };

    @Test
    public void evaluate() throws Exception {
        // given
        doReturn(expression).when(expressionParser).parseExpression("expression");

        doReturn(true).when(expression).getValue(evaluationContext, Boolean.class);

        List<SecurityExpressionHandler> securityExpressionHandlers = Collections.singletonList(securityExpressionHandler);

        // when
        WebSecurityExpressionEvaluator evaluator = new WebSecurityExpressionEvaluator(securityExpressionHandlers);
        boolean result = evaluator.evaluate("expression", httpServletRequest, httpServletResponse);

        // then
        assertThat(result, equalTo(true));
    }
}