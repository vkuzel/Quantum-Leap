package cz.quantumleap.core.view.template;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.expression.IExpressionObjectFactory;

import java.util.Set;

class QuantumLeapExpressionObjectFactory implements IExpressionObjectFactory {

    private static final Set<String> EXPRESSION_OBJECT_NAMES = Set.of(Context.EXPRESSION_OBJECT_NAME);
    private static final Context QL_CONTEXT = new Context();

    @Override
    public Set<String> getAllExpressionObjectNames() {
        return EXPRESSION_OBJECT_NAMES;
    }

    @Override
    public Object buildObject(IExpressionContext context, String expressionObjectName) {
        if (Context.EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            return QL_CONTEXT;
        }
        return null;
    }

    @Override
    public boolean isCacheable(String expressionObjectName) {
        return false;
    }

    private static class Context {

        private static final String EXPRESSION_OBJECT_NAME = "qlctx";

        @SuppressWarnings("unused")
        public String requestUri() {
            var attributes = RequestContextHolder.getRequestAttributes();
            if (attributes instanceof ServletRequestAttributes servletAttributes) {
                return servletAttributes.getRequest().getRequestURI();
            } else {
                throw new IllegalStateException("Not a servlet request: " + attributes);
            }
        }

        @SuppressWarnings("unused")
        public ServletUriComponentsBuilder requestUriBuilder() {
            return ServletUriComponentsBuilder.fromCurrentRequest();
        }
    }
}
