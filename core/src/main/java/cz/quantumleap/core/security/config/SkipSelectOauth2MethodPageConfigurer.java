package cz.quantumleap.core.security.config;

import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer.AuthorizationEndpointConfig;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import java.util.ArrayList;
import java.util.List;

import static cz.quantumleap.core.utils.ReflectionUtils.getClassFieldValue;
import static java.util.Objects.requireNonNull;
import static org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI;

public class SkipSelectOauth2MethodPageConfigurer<H extends HttpSecurityBuilder<H>>
        extends AbstractHttpConfigurer<SkipSelectOauth2MethodPageConfigurer<H>, H> {

    @SuppressWarnings("unchecked")
    @Override
    public void init(H builder) throws Exception {
        var singleAuthenticationUrl = getSingleAuthenticationUrl(builder);
        if (singleAuthenticationUrl != null) {
            ExceptionHandlingConfigurer<H> exceptionHandlingConfigurer = builder.getConfigurer(ExceptionHandlingConfigurer.class);
            requireNonNull(exceptionHandlingConfigurer);

            // There is only one authentication method (authentication url) so skip the login page and redirect user
            // directly to authentication URL.
            var authenticationEntryPoint = new LoginUrlAuthenticationEntryPoint(singleAuthenticationUrl);
            exceptionHandlingConfigurer.authenticationEntryPoint(authenticationEntryPoint);
        }

        super.init(builder);
    }

    private String getSingleAuthenticationUrl(H builder) {
        var clientRegistrations = getClientRegistrations(builder);
        if (clientRegistrations.isEmpty()) {
            // No client registration found, for example in integration test
            return null;
        } else if (clientRegistrations.size() > 1) {
            // Multiple client registration, we need "select auth method" page.
            return null;
        }

        return getAuthorizationRequestBaseUri(builder) + "/" + clientRegistrations.getFirst().getRegistrationId();
    }

    private List<ClientRegistration> getClientRegistrations(H builder) {
        var repository = builder.getSharedObject(ClientRegistrationRepository.class);
        if (repository == null) {
            throw new IllegalStateException("Client registration repository not found!");
        }

        List<ClientRegistration> clientRegistrations = new ArrayList<>();
        if (repository instanceof Iterable<?> iterableRepository) {
            for (Object item : iterableRepository) {
                if (item instanceof ClientRegistration clientRegistration) {
                    clientRegistrations.add(clientRegistration);
                }
            }
        }
        return clientRegistrations;
    }

    // See Oauth2LoginConfigurer.configure()
    @SuppressWarnings("unchecked")
    private String getAuthorizationRequestBaseUri(H builder) {
        OAuth2LoginConfigurer<H> loginConfigurer = builder.getConfigurer(OAuth2LoginConfigurer.class);
        var baseUriHolder = new String[]{DEFAULT_AUTHORIZATION_REQUEST_BASE_URI};
        loginConfigurer.authorizationEndpoint(config -> {
            var fieldName = "authorizationRequestBaseUri";
            var fieldValue = getClassFieldValue(AuthorizationEndpointConfig.class, config, fieldName);
            if (fieldValue instanceof String baseUri) {
                baseUriHolder[0] = baseUri;
            }
        });
        return baseUriHolder[0];
    }
}
