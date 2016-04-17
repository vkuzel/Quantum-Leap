package cz.quantumleap.server.config;

import cz.quantumleap.cli.environment.EnvironmentBuilderService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class TestEnvironmentContext {

    @Bean
    public EnvironmentBuilderService environmentBuilderService() {
        return new EnvironmentBuilderService();
    }

    @PostConstruct
    public void rebuildEnvironment() {
        environmentBuilderService().dropEnvironment();
        environmentBuilderService().buildEnvironment();
    }
}
