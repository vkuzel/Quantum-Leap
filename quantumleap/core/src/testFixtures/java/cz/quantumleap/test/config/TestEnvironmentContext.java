package cz.quantumleap.test.config;

import cz.quantumleap.cli.environment.EnvironmentBuilderService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.PostConstruct;

@Configuration
@ActiveProfiles("test")
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
