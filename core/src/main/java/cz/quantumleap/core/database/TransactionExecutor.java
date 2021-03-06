package cz.quantumleap.core.database;

import org.jooq.DSLContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
public class TransactionExecutor {

    private final DSLContext dslContext;

    public TransactionExecutor(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Transactional
    public void execute(Consumer<DSLContext> consumer) {
        consumer.accept(dslContext);
    }

    @Transactional
    public <T> T execute(Supplier<T> supplier) {
        return supplier.get();
    }

    @Transactional
    public <T> T execute(Function<DSLContext, T> function) {
        return function.apply(dslContext);
    }

    @Transactional
    public void execute(Runnable runnable) {
        runnable.run();
    }
}
