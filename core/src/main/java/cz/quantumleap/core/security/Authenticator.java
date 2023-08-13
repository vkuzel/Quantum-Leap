package cz.quantumleap.core.security;

import org.apache.commons.lang3.Validate;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import java.util.Map;
import java.util.stream.Collectors;

public class Authenticator {

    private final AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();

    public boolean isUserAuthenticated(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !trustResolver.isAnonymous(authentication);
    }

    public String getAuthenticationEmail(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (!authentication.isAuthenticated() || trustResolver.isAnonymous(authentication)) {
            return null;
        }

        var principal = authentication.getPrincipal();
        if (!(principal instanceof DefaultOidcUser oidcUser)) {
            throw new IllegalArgumentException("Unknown principal type " + principal.getClass().getName());
        }

        var attributes = oidcUser.getAttributes();
        var email = (String) attributes.get(DatabaseAuthoritiesLoader.OAUTH_DETAILS_EMAIL);
        Validate.notNull(email, "Email was not found in OidcUser details! " + formatDetails(attributes));

        return email;
    }

    private String formatDetails(Map<String, Object> map) {
        return map.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(", "));
    }
}
