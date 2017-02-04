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
public class DashboardController {

    private final AdminMenuManager adminMenuManager;

    @Autowired
    public DashboardController(AdminMenuManager adminMenuManager) {
        this.adminMenuManager = adminMenuManager;
    }

    @ModelAttribute("adminMenuItems")
    public List<AdminMenuItem> getMenuItems() {
        return adminMenuManager.getMenuItems();
    }

    @AdminMenuItemDefinition(title = "admin.menu.dashboard", fontAwesomeIcon = "fa-dashboard")
    @RequestMapping("/dashboard")
    public String dashboard() {
        return "admin/dashboard";
    }

    @AdminMenuItemDefinition(title = "admin.menu.entrance", fontAwesomeIcon = "fa-sitemap")
    @RequestMapping("/entrance")
    public String skeleton() {
        return "admin/entrance";
    }
}
