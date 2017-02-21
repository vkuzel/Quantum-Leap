package cz.quantumleap.server.security;

import org.jooq.DSLContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RequestMapping;

//@SpringBootTest(classes = MethodSecurityConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ContextConfiguration(classes = WebSecurityTest.class)


@RunWith(SpringRunner.class)
@WebMvcTest(controllers = WebSecurityTest.SecurityTestController.class)
//@Import(WebSecurityConfiguration.class)
@ActiveProfiles("test") // TODO Maybe not necessary...
public class WebSecurityTest {

    @Autowired
    private MockMvc mvc;

//    @MockBean
//    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @MockBean
    private DSLContext dslContext;

    @Test
    public void test() {

    }

    public static class SecurityTestController {

        @RequestMapping("/endpoint")
        public void endpoint() {
        }
    }
}
