package cz.quantumleap.server.security.configurer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UrlSecurityConfigurerTest {

    @Mock
    private ExpressionUrlAuthorizationConfigurer<HttpSecurity>.MvcMatchersAuthorizedUrl mvcMatchersAuthorizedUrl;

    @Test
    public void configure_methodPreAuthorizePrecedence() throws Exception {
        // given
        PreAuthorize methodPreAuthorize = mockPreAuthorize("xxx");
        PreAuthorize typePreAuthorize = mockPreAuthorize("yyy");

        // when
        UrlSecurityConfigurer configurer = new UrlSecurityConfigurer(methodPreAuthorize, typePreAuthorize);
        configurer.configure(mvcMatchersAuthorizedUrl);

        // then
        verify(mvcMatchersAuthorizedUrl, times(1)).access(any());
        verifyNoMoreInteractions(mvcMatchersAuthorizedUrl);
        verify(methodPreAuthorize, times(1)).value();
        verifyZeroInteractions(typePreAuthorize);
    }

    @Test
    public void configure_typePreAuthorize() throws Exception {
        // given
        PreAuthorize methodPreAuthorize = null;
        PreAuthorize typePreAuthorize = mockPreAuthorize("xxx");

        // when
        UrlSecurityConfigurer configurer = new UrlSecurityConfigurer(methodPreAuthorize, typePreAuthorize);
        configurer.configure(mvcMatchersAuthorizedUrl);

        // then
        verify(mvcMatchersAuthorizedUrl, times(1)).access(any());
        verifyNoMoreInteractions(mvcMatchersAuthorizedUrl);
        verify(typePreAuthorize, times(1)).value();
    }

    @Test
    public void equals() throws Exception {
        // given
        PreAuthorize methodPreAuthorize = mockPreAuthorize("xxx");
        PreAuthorize typePreAuthorize = mockPreAuthorize("yyy");

        // when
        UrlSecurityConfigurer configurer = new UrlSecurityConfigurer(methodPreAuthorize, typePreAuthorize);
        UrlSecurityConfigurer other = new UrlSecurityConfigurer(methodPreAuthorize, typePreAuthorize);
        boolean isEquals = configurer.equals(other);

        // then
        assertTrue(isEquals);
    }

    private PreAuthorize mockPreAuthorize(String spEl) {
        PreAuthorize preAuthorize = mock(PreAuthorize.class);
        doReturn(spEl).when(preAuthorize).value();
        return preAuthorize;
    }
}