package cz.quantumleap.server.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.Callable;

@Component
public class TransactionExecutor {

    @Autowired
    private TransactionTemplate transactionTemplate;

    public void execute(Runnable runnable) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                runnable.run();
            }
        });
    }

    public <T> T execute(Callable<T> callable) {
        return transactionTemplate.execute(status -> {
            try {
                return callable.call();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        });
    }
}
