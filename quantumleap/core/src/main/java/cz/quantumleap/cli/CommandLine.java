package cz.quantumleap.cli;

import cz.quantumleap.common.ResourceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;

@Configuration
@ComponentScan(basePackages = {"cz.quantumleap.cli", "cz.quantumleap.common"})
public class CommandLine implements CommandLineRunner {

    @Autowired
    private ResourceManager resourceManager;

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

    public static void main(String[] args) throws IOException {
        SpringApplication application = new SpringApplication(CommandLine.class);
        application.setWebEnvironment(false);
        application.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        // TODO drush ... cz.quantumleap.server, cz.quantumleap.cli
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        // TODO Sort resources by name!
        List<Resource> scripts = resourceManager.findOnClasspath("db/scripts/*_*.sql");
        // TODO Add support for Postgres functions and procedures.
        scripts.forEach(populator::addScript);
        populator.execute(dataSource());
    }
}
