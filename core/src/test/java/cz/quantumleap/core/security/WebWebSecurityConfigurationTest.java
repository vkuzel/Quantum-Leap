package cz.quantumleap.core.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = {
        WebWebSecurityConfigurationTest.SecuredMethodsTestController.class,
        WebWebSecurityConfigurationTest.SecuredTypeTestController.class
})
@Import(WebSecurityConfiguration.class)
@TestPropertySource(properties = "quantumleap.security.loginPageUrl=/test-login-page")
public class WebWebSecurityConfigurationTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @Test
    public void accessAsUnauthenticated() throws Exception {

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
    public void accessAsAuthenticated() throws Exception {
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
    public void accessAsAdmin() throws Exception {
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

    @Controller
    public static class SecuredMethodsTestController {

        @RequestMapping("/endpoint-for-unauthenticated")
        @PreAuthorize("permitAll()")
        @ResponseBody
        public void endpointForUnauthenticated() {
        }

        @RequestMapping("/endpoint-for-authenticated")
        @ResponseBody
        public void endpointForAuthenticated() {
        }

        @GetMapping(value = "/method-endpoint")
        @PreAuthorize("permitAll()")
        @ResponseBody
        public void getMethodEndpointForUnauthenticated() {
        }

        @PostMapping(value = "/method-endpoint")
        @ResponseBody
        public void postMethodEndpointForAuthenticated() {
        }

        @RequestMapping("/endpoint-for-admin")
        @PreAuthorize("hasRole('ADMIN')")
        @ResponseBody
        public void endpointForAdmin() {
        }

        @RequestMapping({"/assets"})
        @ResponseBody
        public void staticContentEndpointForUnauthenticated() {
        }
    }

    @Controller
    @PreAuthorize("hasRole('ADMIN')")
    public static class SecuredTypeTestController {

        @RequestMapping("/type-endpoint-for-unauthenticated")
        @PreAuthorize("permitAll()")
        @ResponseBody
        public void endpointForUnauthenticated() {
        }

        @RequestMapping("/type-endpoint-for-admin")
        @ResponseBody
        public void endpointForAuthenticated() {
        }
    }
}
