//package com.vkuzel.quantumleap.test_module.thymeleaf;
//
//import com.google.common.collect.ImmutableMap;
//import com.vkuzel.quantumleap.common.WithQuantumLeapContext;
//import org.junit.Assert;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationContext;
//import org.springframework.server.io.Resource;
//import org.springframework.mock.web.MockHttpServletRequest;
//import org.springframework.mock.web.MockHttpServletResponse;
//import org.springframework.mock.web.MockServletContext;
//import org.thymeleaf.context.WebContext;
//import org.thymeleaf.spring4.SpringTemplateEngine;
//
//import javax.servlet.ServletContext;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.*;
//import java.util.Locale;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//public class TemplateProcessingTests extends WithQuantumLeapContext {
//
//    @Autowired
//    private SpringTemplateEngine thymeleafEngine;
//
//    @Autowired
//    private ApplicationContext applicationContext;
//
//    @Test
//    public void testDecorateHomePage() throws IOException {
//        Map<String, Object> model = ImmutableMap.of("currentTime", 0);
//        WebContext context = createWebContext(model);
//
//        Writer processedTemplate = new StringWriter();
//        thymeleafEngine.process("test/home", context, processedTemplate);
//
//        Assert.assertEquals(sanitize(readResource("classpath:themes/decorated_home_expected_result.html")), sanitize(processedTemplate.toString()));
//    }
//
//    private WebContext createWebContext(Map<String, Object> variables) {
//        HttpServletRequest request = new MockHttpServletRequest();
//        HttpServletResponse response = new MockHttpServletResponse();
//        ServletContext servletContext = new MockServletContext();
//        return new WebContext(request, response, servletContext, Locale.getDefault(), variables);
//    }
//
//    private String readResource(String resourceLocation) throws IOException {
//        Resource resource = applicationContext.getResource(resourceLocation);
//        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
//            return buffer.lines().collect(Collectors.joining(System.lineSeparator()));
//        }
//    }
//
//    private String sanitize(String text) {
//        return text.replaceAll("\\s+", "");
//    }
//}
