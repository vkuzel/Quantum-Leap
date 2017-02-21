package cz.quantumleap.server.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = {
        WebSecurityConfigurationTest.SecuredMethodsTestController.class,
        WebSecurityConfigurationTest.SecuredTypeTestController.class
})
@Import(SecurityConfiguration.class)
public class WebSecurityConfigurationTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ResourceServerProperties resourceServerProperties;

    @Test
    public void accessAsUnauthenticated() throws Exception {

        mvc.perform(get("/endpoint-for-unauthenticated"))
                .andExpect(status().isOk());

        mvc.perform(get("/endpoint-for-authenticated"))
                .andExpect(status().isForbidden());

        mvc.perform(get("/method-endpoint"))
                .andExpect(status().isOk());

        mvc.perform(post("/method-endpoint")
                .with(csrf()))
                .andExpect(status().isForbidden());

        mvc.perform(get("/endpoint-for-admin"))
                .andExpect(status().isForbidden());

        mvc.perform(get("/login"))
                .andExpect(status().isOk()); // TODO Login should redirect to xxx

        mvc.perform(get("/assets"))
                .andExpect(status().isOk());

        mvc.perform(get("/type-endpoint-for-unauthenticated"))
                .andExpect(status().isOk());

        mvc.perform(get("/type-endpoint-for-admin"))
                .andExpect(status().isForbidden());
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

        mvc.perform(get("/login"))
                .andExpect(status().isOk()); // TODO Login should redirect to xxx

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

        mvc.perform(get("/login"))
                .andExpect(status().isOk()); // TODO Login should redirect to xxx

        mvc.perform(get("/assets"))
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

        @RequestMapping(value = "/method-endpoint", method = RequestMethod.GET)
        @PreAuthorize("permitAll()")
        @ResponseBody
        public void getMethodEndpointForUnauthenticated() {
        }

        @RequestMapping(value = "/method-endpoint", method = RequestMethod.POST)
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
