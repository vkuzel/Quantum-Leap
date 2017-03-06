package cz.quantumleap.server.admin.dashboard;

import cz.quantumleap.server.admin.AdminController;
import cz.quantumleap.server.admin.menu.AdminMenuItemDefinition;
import cz.quantumleap.server.admin.menu.AdminMenuManager;
import cz.quantumleap.server.security.WebSecurityExpressionEvaluator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController extends AdminController {

    public DashboardController(AdminMenuManager adminMenuManager, WebSecurityExpressionEvaluator webSecurityExpressionEvaluator) {
        super(adminMenuManager, webSecurityExpressionEvaluator);
    }

    @AdminMenuItemDefinition(title = "admin.menu.dashboard", fontAwesomeIcon = "fa-dashboard")
    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/dashboard";
    }
}
