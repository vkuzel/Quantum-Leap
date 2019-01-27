package cz.quantumleap.core.security.mock;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class SecuredTypeTestController {

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
