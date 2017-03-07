package cz.quantumleap.demo;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class DemoController {

    @GetMapping("/")
    @PreAuthorize("permitAll()")
    public String index() {
        return "demo/entry";
    }

    @PreAuthorize("hasAnyRole('OBSERVER', 'ROLE_MASTER')")
    @GetMapping(path = {"/first", "/second"})
    @ResponseBody
    public String twoMappings() {
        return "two mappings";
    }

    @PostMapping("/first")
    public String oneMapping() {
        return "one mapping";
    }

    @RequestMapping(path = "/two-methods", method = {RequestMethod.GET, RequestMethod.POST})
//    @PreAuthorize("permitAll()")
    @ResponseBody
    public String twoMethods() {
        return "two methods";
    }
}
