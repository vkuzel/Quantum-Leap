package cz.quantumleap.server.admin;

import cz.quantumleap.server.admin.menu.AdminMenuItem;
import cz.quantumleap.server.admin.menu.AdminMenuItemDefinition;
import cz.quantumleap.server.admin.menu.AdminMenuManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class DashboardController extends AdminController {

    public DashboardController(AdminMenuManager adminMenuManager) {
        super(adminMenuManager);
    }

    @AdminMenuItemDefinition(title = "admin.menu.dashboard", fontAwesomeIcon = "fa-dashboard")
    @RequestMapping("/dashboard")
    public String dashboard() {
        return "admin/dashboard";
    }

    @AdminMenuItemDefinition(title = "admin.menu.entrance", fontAwesomeIcon = "fa-sitemap")
    @RequestMapping("/login")
    public String skeleton() {
        return "admin/login";
    }
}
