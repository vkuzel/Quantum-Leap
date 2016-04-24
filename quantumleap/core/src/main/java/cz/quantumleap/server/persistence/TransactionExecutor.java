package cz.quantumleap.server.persistence;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.Callable;

@Component
public class TransactionExecutor {

    @Transactional
    public void execute(Runnable runnable) {
        runnable.run();
    }

    @Transactional
    public <T> T execute(Callable<T> callable) {
        try {
            return callable.call();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
