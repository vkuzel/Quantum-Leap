package cz.quantumleap.server.admin;

import cz.quantumleap.server.admin.menu.AdminMenuItem;
import cz.quantumleap.server.admin.menu.AdminMenuManager;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

public abstract class AdminController {

    private final AdminMenuManager adminMenuManager;

    public AdminController(AdminMenuManager adminMenuManager) {
        this.adminMenuManager = adminMenuManager;
    }

    @ModelAttribute("adminMenuItems")
    public List<AdminMenuItem> getMenuItems() {
        return adminMenuManager.getMenuItems();
    }

}
