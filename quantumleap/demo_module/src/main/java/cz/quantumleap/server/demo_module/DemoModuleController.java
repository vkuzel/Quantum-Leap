package cz.quantumleap.server.demo_module;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;

@Controller
public class DemoModuleController {

    @ModelAttribute("currentTime")
    public LocalDateTime currentTime() {
        return LocalDateTime.now();
    }

    @RequestMapping("/sitemap")
    public String sitemap() {
        return "demo/sitemap";
    }
}
