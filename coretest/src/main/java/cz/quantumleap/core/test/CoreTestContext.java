package cz.quantumleap.core.test;

import cz.quantumleap.cli.environment.EnvironmentBuilder;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ComponentScan(basePackages = {"cz.quantumleap.core", "cz.quantumleap.cli.environment"})
public class CoreTestContext {

    private final EnvironmentBuilder environmentBuilder;

    @MockitoBean
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
