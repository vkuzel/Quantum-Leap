package cz.quantumleap.core.security;

import cz.quantumleap.core.security.config.IgnoreCsrfProtectionRequestMatcher;
import cz.quantumleap.core.security.config.MappingInfoRequestMatcher;
import cz.quantumleap.core.security.config.SkipLoginPageEntryPointConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ConditionalOnWebApplication
public class WebSecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(
            @Value("${quantumleap.security.loginPageUrl:/}") String loginPageUrl,
            RequestMappingHandlerMapping requestMappingHandlerMapping,
            HttpSecurity httpSecurity
    ) throws Exception {
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
        httpSecurity
                .authorizeRequests()
                .requestMatchers(new MappingInfoRequestMatcher.Builder(handlerMethods).build())
                .authenticated()
                .and().csrf().ignoringRequestMatchers(new IgnoreCsrfProtectionRequestMatcher.Builder(handlerMethods).build())
                .and().oauth2Login().loginPage(loginPageUrl)
                .and().apply(new SkipLoginPageEntryPointConfigurer<>()) // This prevents configuring default login page in DefaultLoginPageConfigurer.configure()
                .and().logout().logoutSuccessUrl("/");
        return httpSecurity.build();
    }
}
