package cz.quantumleap.server.security;

import cz.quantumleap.server.security.configurer.RequestMappingAwareHttpSecurityConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
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
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    SecurityConfiguration(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();

        RequestMappingAwareHttpSecurityConfigurer requestMappingAwareHttpSecurityConfigurer = new RequestMappingAwareHttpSecurityConfigurer(handlerMethods);
        requestMappingAwareHttpSecurityConfigurer.configure(httpSecurity);
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .inMemoryAuthentication()
                .withUser("user").password("password").roles("ADMIN", "OBSERVER");
    }
}
