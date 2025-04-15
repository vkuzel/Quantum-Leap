package cz.quantumleap.core.security;

import cz.quantumleap.core.security.mock.SecuredMethodsTestController;
import cz.quantumleap.core.security.mock.SecuredTypeTestController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({
        SecuredMethodsTestController.class,
        SecuredTypeTestController.class
})
@Import(WebSecurityConfiguration.class)
@TestPropertySource(properties = "quantumleap.security.loginPageUrl=/test-login-page")
public class WebWebSecurityConfigurationTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    @SuppressWarnings("unused")
    private ClientRegistrationRepository clientRegistrationRepository;

    @Test
    public void unauthenticatedUserDoesNotHaveAccessToProtectedPages() throws Exception {
        mvc.perform(get("/endpoint-for-unauthenticated"))
                .andExpect(status().isOk());

        mvc.perform(get("/endpoint-for-authenticated"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/test-login-page"));

        mvc.perform(get("/method-endpoint"))
                .andExpect(status().isOk());

        mvc.perform(post("/method-endpoint")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/test-login-page"));

        mvc.perform(get("/endpoint-for-admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/test-login-page"));

        mvc.perform(get("/type-endpoint-for-unauthenticated"))
                .andExpect(status().isOk());

        mvc.perform(get("/type-endpoint-for-admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/test-login-page"));
    }

    @Test
    @WithMockUser
    public void authenticatedUserHasLimitedAccess() throws Exception {
        mvc.perform(get("/endpoint-for-unauthenticated"))
                .andExpect(status().isOk());

        mvc.perform(get("/endpoint-for-authenticated"))
                .andExpect(status().isOk());

        mvc.perform(get("/method-endpoint"))
                .andExpect(status().isOk());

        mvc.perform(post("/method-endpoint"))
                .andExpect(status().isForbidden());

        mvc.perform(post("/method-endpoint")
                .with(csrf()))
                .andExpect(status().isOk());

        mvc.perform(post("/method-endpoint")
                .with(csrf().useInvalidToken()))
                .andExpect(status().isForbidden());

        mvc.perform(get("/endpoint-for-admin"))
                .andExpect(status().isForbidden());

        mvc.perform(get("/assets"))
                .andExpect(status().isOk());

        mvc.perform(get("/type-endpoint-for-unauthenticated"))
                .andExpect(status().isOk());

        mvc.perform(get("/type-endpoint-for-admin"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void adminHasAccessEverywhere() throws Exception {
        mvc.perform(get("/endpoint-for-unauthenticated"))
                .andExpect(status().isOk());

        mvc.perform(get("/endpoint-for-authenticated"))
                .andExpect(status().isOk());

        mvc.perform(get("/method-endpoint"))
                .andExpect(status().isOk());

        mvc.perform(post("/method-endpoint")
                .with(csrf()))
                .andExpect(status().isOk());

        mvc.perform(get("/endpoint-for-admin"))
                .andExpect(status().isOk());

        mvc.perform(get("/type-endpoint-for-unauthenticated"))
                .andExpect(status().isOk());

        mvc.perform(get("/type-endpoint-for-admin"))
                .andExpect(status().isOk());
    }
}
