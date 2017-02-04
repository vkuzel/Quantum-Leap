package cz.quantumleap.server.dashboard;

import cz.quantumleap.server.dashboard.menu.DashboardMenuItem;
import cz.quantumleap.server.dashboard.menu.DashboardMenuItemDefinition;
import cz.quantumleap.server.dashboard.menu.DashboardMenuManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class DashboardController {

    private final DashboardMenuManager dashboardMenuManager;

    @Autowired
    public DashboardController(DashboardMenuManager dashboardMenuManager) {
        this.dashboardMenuManager = dashboardMenuManager;
    }

    @ModelAttribute("dashboardMenuItems")
    public List<DashboardMenuItem> getMenuItems() {
        return dashboardMenuManager.getMenuItems();
    }

    @DashboardMenuItemDefinition(title = "dashboard.menu.dashboard", fontAwesomeIcon = "fa-dashboard")
    @RequestMapping("/dashboard")
    public String dashboard() {
        return "dashboard/dashboard";
    }

    @DashboardMenuItemDefinition(title = "dashboard.menu.entrance", fontAwesomeIcon = "fa-sitemap")
    @RequestMapping("/entrance")
    public String skeleton() {
        return "dashboard/entrance";
    }
}
