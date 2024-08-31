package cz.quantumleap.core.view.template;

import cz.quantumleap.core.filestorage.FileStorageManager;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;
import org.thymeleaf.processor.IProcessor;

import java.util.Collections;
import java.util.Set;

public class QuantumLeapDialect extends AbstractProcessorDialect implements IExpressionObjectDialect {

    private static final String DIALECT_NAME = "Quantum Leap dialect";
    private static final String DIALECT_PREFIX = "ql";
    private static final int PRECEDENCE = 1000;
    private static final IExpressionObjectFactory QL_EXPRESSION_OBJECT_FACTORY = new QuantumLeapExpressionObjectFactory();

    private final FileStorageManager fileStorageManager;

    public QuantumLeapDialect(FileStorageManager fileStorageManager) {
        super(DIALECT_NAME, DIALECT_PREFIX, PRECEDENCE);
        this.fileStorageManager = fileStorageManager;
    }

    @Override
    public Set<IProcessor> getProcessors(String dialectPrefix) {
        return Collections.singleton(new ResizableImageTagProcessor(dialectPrefix, fileStorageManager));
    }

    @Override
    public IExpressionObjectFactory getExpressionObjectFactory() {
        return QL_EXPRESSION_OBJECT_FACTORY;
    }
}
