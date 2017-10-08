package cz.quantumleap.core.security;

import cz.quantumleap.core.security.configurer.RequestMappingAwareHttpSecurityConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableOAuth2Sso
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ConditionalOnWebApplication
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    WebSecurityConfiguration(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();

        RequestMappingAwareHttpSecurityConfigurer requestMappingAwareHttpSecurityConfigurer = new RequestMappingAwareHttpSecurityConfigurer(handlerMethods);
        requestMappingAwareHttpSecurityConfigurer.configure(httpSecurity);

        httpSecurity.logout().logoutSuccessUrl("/");
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        // We use the OAuth 2, so authentication manager can be disabled: http://docs.spring.io/spring-boot/docs/current/reference/html/howto-security.html
        return null;
    }
}
