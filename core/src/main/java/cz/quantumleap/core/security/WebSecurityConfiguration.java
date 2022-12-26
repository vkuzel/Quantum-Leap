package cz.quantumleap.core.security;

import cz.quantumleap.core.security.config.RequestMappingInfoFilters;
import cz.quantumleap.core.security.config.RequestMappingInfoRequestMatcher;
import cz.quantumleap.core.security.config.SkipLoginPageEntryPointConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
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
                .authorizeHttpRequests()
                .requestMatchers(resourcesRequestMatchers()).permitAll()
                .requestMatchers(permitAllMappingInfoMatcher(handlerMethods)).permitAll()
                .anyRequest().authenticated()
                .and().csrf().ignoringRequestMatchers(ignoreCsrfMappingInfoMatcher(handlerMethods))
                .and().oauth2Login().loginPage(loginPageUrl)
                // This prevents configuring default login page in DefaultLoginPageConfigurer.configure()
                .and().apply(new SkipLoginPageEntryPointConfigurer<>())
                .and().logout().logoutSuccessUrl("/");
        return httpSecurity.build();
    }

    private RequestMatcher[] resourcesRequestMatchers() {
        return new RequestMatcher[] {
                new AntPathRequestMatcher("/assets/**"),
                new AntPathRequestMatcher("/webjars/**"),
                new AntPathRequestMatcher("/storage/**"),
        };
    }

    private RequestMatcher permitAllMappingInfoMatcher(Map<RequestMappingInfo, HandlerMethod> handlerMethods) {
        List<RequestMappingInfo> filteredHandlerMethods = handlerMethods.entrySet().stream()
                .filter(RequestMappingInfoFilters::hasPermitAll)
                .map(Entry::getKey)
                .toList();
        return new RequestMappingInfoRequestMatcher(filteredHandlerMethods);
    }

    private RequestMatcher ignoreCsrfMappingInfoMatcher(Map<RequestMappingInfo, HandlerMethod> handlerMethods) {
        List<RequestMappingInfo> filteredHandlerMethods = handlerMethods.entrySet().stream()
                .filter(RequestMappingInfoFilters::hasIgnoreCsrf)
                .map(Entry::getKey)
                .toList();
        return new RequestMappingInfoRequestMatcher(filteredHandlerMethods);
    }
}
