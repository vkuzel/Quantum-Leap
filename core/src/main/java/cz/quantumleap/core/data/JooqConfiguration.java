package cz.quantumleap.core.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.quantumleap.core.data.converter.JooqConverterProvider;
import org.jooq.ConnectionProvider;
import org.jooq.ExecuteListenerProvider;
import org.jooq.RecordListenerProvider;
import org.jooq.TransactionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.boot.autoconfigure.jooq.JooqProperties;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * This configuration exists, because current jOOQ auto-configuration does not
 * support converter provider. As soon as https://github.com/spring-projects/spring-boot/pull/24485
 * will be merged, this class can be removed.
 */
@Configuration
public class JooqConfiguration extends DefaultConfiguration {

    public JooqConfiguration(
            JooqProperties properties,
            ConnectionProvider connectionProvider,
            DataSource dataSource,
            TransactionProvider transactionProvider,
            RecordListenerProvider recordListenerProvider,
            ExecuteListenerProvider executeListenerProvider,
            ObjectMapper objectMapper
    ) {
        super();
        set(properties.determineSqlDialect(dataSource));
        set(connectionProvider);
        set(transactionProvider);
        set(recordListenerProvider);
        set(executeListenerProvider);
        set(new JooqConverterProvider(converterProvider(), objectMapper));
    }
}
