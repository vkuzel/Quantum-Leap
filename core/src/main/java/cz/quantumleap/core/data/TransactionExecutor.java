package cz.quantumleap.core.data;

import org.jooq.DSLContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;
import java.util.function.Function;

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
    public <T> T execute(Function<DSLContext, T> function) {
        return function.apply(dslContext);
    }

//    @Transactional
//    public void execute(Runnable runnable) {
//        try {
//            runnable.run();
//        } catch (Exception e ) {
//            throw e;
//        }
//    }
//
//    @Transactional
//    public <T> T execute(Callable<T> callable) {
//        try {
//            return callable.call();
//        } catch (Exception e) {
//            throw new IllegalStateException(e);
//        }
//    }
}
