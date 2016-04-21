package cz.quantumleap.cli.config;

import cz.quantumleap.server.autoincrement.IncrementsService;
import cz.quantumleap.server.common.ModuleDependencyManager;
import cz.quantumleap.server.common.ResourceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.sql.DataSource;

@Configuration
public class CliContext {

    @Autowired
    private Environment environment;

    @Bean
    public ModuleDependencyManager moduleDependencyManager() {
        return new ModuleDependencyManager();
    }

    @Bean
    public ResourceManager resourceManager() {
        return new ResourceManager();
    }

    @Bean
    public IncrementsService incrementsManager() {
        return new IncrementsService();
    }

    @Bean
    public DataSource dataSource() {
        SingleConnectionDataSource dataSource = new SingleConnectionDataSource(
                environment.getProperty("spring.datasource.url"),
                environment.getProperty("spring.datasource.username"),
                environment.getProperty("spring.datasource.password"),
                false
        );
        dataSource.setAutoCommit(environment.getProperty("spring.datasource.defaultAutoCommit", Boolean.class));
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }
}
