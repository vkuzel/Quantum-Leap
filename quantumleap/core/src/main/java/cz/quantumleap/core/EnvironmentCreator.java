package cz.quantumleap.core;

import cz.quantumleap.core.common.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;

@Configuration
public class EnvironmentCreator implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(EnvironmentCreator.class);

    public static void main(String[] args) throws IOException {
        SpringApplication application = new SpringApplication(EnvironmentCreator.class);
        application.setWebEnvironment(false);
        application.run(args);
    }

    @Bean
    public ResourceManager resourceManager() {
        return new ResourceManager();
    }

    @Autowired
    private Environment environment;

    @Bean
    public DataSource dataSource() {
        return new SingleConnectionDataSource(
                environment.getProperty("spring.datasource.url"),
                environment.getProperty("spring.datasource.username"),
                environment.getProperty("spring.datasource.password"),
                false
        );
    }

    @Override
    public void run(String... args) throws Exception {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        List<Resource> scripts = resourceManager().findOnClasspath("db/scripts/*_*.sql");
        scripts.forEach(populator::addScript);
        populator.execute(dataSource());
    }
}
