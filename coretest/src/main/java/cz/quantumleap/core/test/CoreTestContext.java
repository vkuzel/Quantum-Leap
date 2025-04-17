package cz.quantumleap.core.test;

import cz.quantumleap.cli.environment.EnvironmentBuilder;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Component;

@ComponentScan(basePackages = {"cz.quantumleap.core", "cz.quantumleap.cli.environment"})
public class CoreTestContext {

    private final EnvironmentBuilder environmentBuilder;

    public CoreTestContext(EnvironmentBuilder environmentBuilder) {
        this.environmentBuilder = environmentBuilder;
    }

    @PostConstruct
    public void rebuildEnvironment() {
        environmentBuilder.dropEnvironment();
        environmentBuilder.buildEnvironment();
    }

    // @MockitoBean is not resolved in a test-context class, so the class is
    // stubbed manually
    @Component
    public static class ClientRegistrationRepositoryStub implements ClientRegistrationRepository {
        @Override
        public ClientRegistration findByRegistrationId(String registrationId) {
            throw new UnsupportedOperationException();
        }
    }
}
