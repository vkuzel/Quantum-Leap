package cz.quantumleap.core.security.config;

import cz.quantumleap.core.common.ReflectionUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.core.ResolvableType;
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

import static java.util.Objects.requireNonNullElse;
import static org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI;

public class SkipLoginPageEntryPointConfigurer<H extends HttpSecurityBuilder<H>> extends AbstractHttpConfigurer<SkipLoginPageEntryPointConfigurer<H>, H> {

    @SuppressWarnings("unchecked")
    @Override
    public void init(H builder) throws Exception {
        String singleAuthenticationUrl = getSingleAuthenticationUrl(builder);
        if (singleAuthenticationUrl != null) {
            ExceptionHandlingConfigurer<H> exceptionHandlingConfigurer = builder.getConfigurer(ExceptionHandlingConfigurer.class);
            Validate.notNull(exceptionHandlingConfigurer);

            // There is only one authentication method (authentication url) so skip the login page and redirect user
            // directly to authentication URL.
            LoginUrlAuthenticationEntryPoint authenticationEntryPoint = new LoginUrlAuthenticationEntryPoint(singleAuthenticationUrl);
            exceptionHandlingConfigurer.authenticationEntryPoint(authenticationEntryPoint);
        }

        super.init(builder);
    }

    @SuppressWarnings("unchecked")
    private String getSingleAuthenticationUrl(H builder) {
        ClientRegistrationRepository clientRegistrationRepository = builder.getSharedObject(ClientRegistrationRepository.class);
        ResolvableType type = ResolvableType.forInstance(clientRegistrationRepository).as(Iterable.class);
        if (type == ResolvableType.NONE || !ClientRegistration.class.isAssignableFrom(type.resolveGenerics()[0])) {
            return null;
        }

        Iterable<ClientRegistration> clientRegistrationIterable = (Iterable<ClientRegistration>) clientRegistrationRepository;
        List<ClientRegistration> clientRegistrations = new ArrayList<>();
        clientRegistrationIterable.forEach(clientRegistrations::add);
        if (clientRegistrations.size() != 1) {
            return null;
        }

        return getAuthorizationRequestBaseUri(builder) + "/" + clientRegistrations.get(0).getRegistrationId();
    }

    // See Oauth2LoginConfigurer.configure()
    @SuppressWarnings("unchecked")
    private String getAuthorizationRequestBaseUri(H builder) {
        AuthorizationEndpointConfig authorizationEndpointConfig = builder.getConfigurer(OAuth2LoginConfigurer.class).authorizationEndpoint();
        String fieldsName = "authorizationRequestBaseUri";
        String authorizationUri = (String) ReflectionUtils.getClassFieldValue(
                AuthorizationEndpointConfig.class,
                authorizationEndpointConfig,
                fieldsName
        );
        return requireNonNullElse(authorizationUri, DEFAULT_AUTHORIZATION_REQUEST_BASE_URI);
    }
}
