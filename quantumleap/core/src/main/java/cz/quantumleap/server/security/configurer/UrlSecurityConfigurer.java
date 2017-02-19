package cz.quantumleap.server.security.configurer;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

import java.util.Objects;

class UrlSecurityConfigurer {

    private final PreAuthorize methodPreAuthorize;
    private final PreAuthorize typePreAuthorize;

    UrlSecurityConfigurer(PreAuthorize methodPreAuthorize, PreAuthorize typePreAuthorize) {
        this.methodPreAuthorize = methodPreAuthorize;
        this.typePreAuthorize = typePreAuthorize;
    }

    ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry configure(
            ExpressionUrlAuthorizationConfigurer<HttpSecurity>.MvcMatchersAuthorizedUrl authorizedUrl
    ) {
        if (methodPreAuthorize != null) {
            return authorizedUrl.access(methodPreAuthorize.value());
        } else {
            return authorizedUrl.access(typePreAuthorize.value());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UrlSecurityConfigurer that = (UrlSecurityConfigurer) o;

        String methodPreAuthorizeSpEl = normalizePreAuthorizeSpEl(methodPreAuthorize);
        String typePreAuthorizeSpEl = normalizePreAuthorizeSpEl(typePreAuthorize);

        return Objects.equals(methodPreAuthorizeSpEl, normalizePreAuthorizeSpEl(that.methodPreAuthorize))
                && Objects.equals(typePreAuthorizeSpEl, normalizePreAuthorizeSpEl(that.typePreAuthorize));
    }

    @Override
    public int hashCode() {
        String methodPreAuthorizeSpEl = normalizePreAuthorizeSpEl(methodPreAuthorize);
        String typePreAuthorizeSpEl = normalizePreAuthorizeSpEl(typePreAuthorize);

        int result = methodPreAuthorizeSpEl != null ? methodPreAuthorizeSpEl.hashCode() : 0;
        result = 31 * result + (typePreAuthorizeSpEl != null ? typePreAuthorizeSpEl.hashCode() : 0);
        return result;
    }

    private String normalizePreAuthorizeSpEl(PreAuthorize preAuthorize) {
        return preAuthorize != null ? preAuthorize.value().trim() : null;
    }
}
