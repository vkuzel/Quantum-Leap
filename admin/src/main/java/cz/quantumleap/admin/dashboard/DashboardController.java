package cz.quantumleap.admin.dashboard;

import cz.quantumleap.admin.AdminController;
import cz.quantumleap.admin.menu.AdminMenuItemDefinition;
import cz.quantumleap.admin.menu.AdminMenuManager;
import cz.quantumleap.core.security.WebSecurityExpressionEvaluator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController extends AdminController {

    public DashboardController(AdminMenuManager adminMenuManager, WebSecurityExpressionEvaluator webSecurityExpressionEvaluator) {
        super(adminMenuManager, webSecurityExpressionEvaluator);
    }

    @AdminMenuItemDefinition(title = "admin.menu.dashboard", fontAwesomeIcon = "fa-dashboard", priority = Integer.MAX_VALUE)
    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/dashboard";
    }
}
