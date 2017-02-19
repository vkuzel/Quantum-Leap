package cz.quantumleap.server.security.configurer;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import cz.quantumleap.server.security.configurer.RequestMappingAwareHttpSecurityConfigurer.UrlSecurityMethod;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

import java.util.Collections;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RequestMappingAwareHttpSecurityConfigurerTest {

    @Mock
    private ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry;

    @Test
    public void configureUrlMatcher_patternWithoutMethod() throws Exception {
        // given
        UrlSecurityConfigurer urlSecurityConfigurer = mock(UrlSecurityConfigurer.class);
        UrlSecurityMethod urlSecurityMethod = new UrlSecurityMethod(urlSecurityConfigurer, null);

        Multimap<UrlSecurityMethod, String> urlSecurityMethodPatterns = ImmutableMultimap.of(urlSecurityMethod, "xxx");

        // when
        RequestMappingAwareHttpSecurityConfigurer configurer = new RequestMappingAwareHttpSecurityConfigurer(Collections.emptyMap());
        configurer.configureUrlMatcher(urlSecurityMethodPatterns, registry);

        // then
        verify(registry, times(1)).mvcMatchers(anyVararg());
        verifyNoMoreInteractions(registry);
        verify(urlSecurityConfigurer, times(1)).configure(any());
        verifyNoMoreInteractions(urlSecurityConfigurer);
    }

    @Test
    public void configureUrlMatcher_patternWithMethod() throws Exception {
        // given
        UrlSecurityConfigurer urlSecurityConfigurer = mock(UrlSecurityConfigurer.class);
        UrlSecurityMethod urlSecurityMethod = new UrlSecurityMethod(urlSecurityConfigurer, HttpMethod.GET);

        Multimap<UrlSecurityMethod, String> urlSecurityMethodPatterns = ImmutableMultimap.of(urlSecurityMethod, "xxx");

        // when
        RequestMappingAwareHttpSecurityConfigurer configurer = new RequestMappingAwareHttpSecurityConfigurer(Collections.emptyMap());
        configurer.configureUrlMatcher(urlSecurityMethodPatterns, registry);

        // then
        verify(registry, times(1)).mvcMatchers(eq(HttpMethod.GET), anyVararg());
        verifyNoMoreInteractions(registry);
        verify(urlSecurityConfigurer, times(1)).configure(any());
        verifyNoMoreInteractions(urlSecurityConfigurer);
    }
}