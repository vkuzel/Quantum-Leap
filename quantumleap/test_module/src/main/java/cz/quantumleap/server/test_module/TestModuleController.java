package cz.quantumleap.server.test_module;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;

@Controller
public class TestModuleController {

    @ModelAttribute("currentTime")
    public LocalDateTime currentTime() {
        return LocalDateTime.now();
    }

    volatile int i = (int) Math.round(Math.random() * 10000);

    @RequestMapping(value = "/")
    public String index() {
        return "test/home";
    }

    @RequestMapping(value = "/sitemap")
    public String sitemap() {
        return "test/sitemap";
    }
}
