package cz.quantumleap.cli.persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.sql.DataSource;

@Configuration
public class PersistenceConfiguration {

    @Autowired
    private Environment environment;

    @Bean
    public DataSource dataSource() {
        // TODO Test autocommit behaviour! Especially committing on DDL.
        return new SingleConnectionDataSource(
                environment.getProperty("spring.datasource.url"),
                environment.getProperty("spring.datasource.username"),
                environment.getProperty("spring.datasource.password"),
                false
        );
    }
}
