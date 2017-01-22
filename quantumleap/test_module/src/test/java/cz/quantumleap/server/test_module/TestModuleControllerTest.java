package cz.quantumleap.server.test_module;

import org.jooq.DSLContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(TestModuleController.class)
@ActiveProfiles("test")
public class TestModuleControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private DSLContext dslContext;

    @Test
    public void index() throws Exception {
        mvc
                .perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Test page")));
    }
}