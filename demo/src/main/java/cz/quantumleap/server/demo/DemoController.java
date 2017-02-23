package cz.quantumleap.server.demo;

import cz.quantumleap.server.admin.menu.AdminMenuItemDefinition;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DemoController {

    @AdminMenuItemDefinition(title = "admin.menu.entrance", fontAwesomeIcon = "fa-sitemap")
    @RequestMapping("/")
    @PreAuthorize("permitAll()")
    public String index() {
        return "demo/entry";
    }

    @PreAuthorize("hasAnyRole('OBSERVER', 'ROLE_MASTER')")
    @RequestMapping(path = {"/first", "/second"})
    @ResponseBody
    public String twoMappings() {
        return "two mappings";
    }

    @RequestMapping(path = {"/first"}, method = RequestMethod.POST)
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
