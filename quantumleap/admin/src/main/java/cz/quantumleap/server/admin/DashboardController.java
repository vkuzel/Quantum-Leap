package cz.quantumleap.server.admin;

import cz.quantumleap.server.admin.menu.AdminMenuItemDefinition;
import cz.quantumleap.server.admin.menu.AdminMenuManager;
import cz.quantumleap.server.security.WebSecurityExpressionEvaluator;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DashboardController extends AdminController {

    public DashboardController(AdminMenuManager adminMenuManager, WebSecurityExpressionEvaluator webSecurityExpressionEvaluator) {
        super(adminMenuManager, webSecurityExpressionEvaluator);
    }

    @AdminMenuItemDefinition(title = "admin.menu.dashboard", fontAwesomeIcon = "fa-dashboard")
    @RequestMapping("/dashboard")
    public String dashboard() {
        return "admin/dashboard";
    }

    @AdminMenuItemDefinition(title = "admin.menu.entrance", fontAwesomeIcon = "fa-sitemap")
    @RequestMapping("/")
    @PreAuthorize("permitAll()")
    public String skeleton() {
        return "admin/login";
    }
}
