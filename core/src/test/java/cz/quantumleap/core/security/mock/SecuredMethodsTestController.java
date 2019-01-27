package cz.quantumleap.core.security.mock;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SecuredMethodsTestController {

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
