package cz.quantumleap.core.test;

import cz.quantumleap.cli.environment.EnvironmentBuilder;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import javax.annotation.PostConstruct;

@ComponentScan(basePackages = {"cz.quantumleap.core", "cz.quantumleap.cli.environment"})
public class CoreTestContext {

    private final EnvironmentBuilder environmentBuilder;

    @MockBean
    private ClientRegistrationRepository clientRegistrationRepository;

    public CoreTestContext(EnvironmentBuilder environmentBuilder) {
        this.environmentBuilder = environmentBuilder;
    }

    @PostConstruct
    public void rebuildEnvironment() {
        environmentBuilder.dropEnvironment();
        environmentBuilder.buildEnvironment();
    }
}
