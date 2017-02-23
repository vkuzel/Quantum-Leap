package cz.quantumleap.server.admin;

import cz.quantumleap.server.admin.menu.AdminMenuItem;
import cz.quantumleap.server.admin.menu.AdminMenuManager;
import cz.quantumleap.server.security.WebSecurityExpressionEvaluator;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AdminController {

    private final AdminMenuManager adminMenuManager;
    private final WebSecurityExpressionEvaluator webSecurityExpressionEvaluator;

    AdminController(AdminMenuManager adminMenuManager, WebSecurityExpressionEvaluator webSecurityExpressionEvaluator) {
        this.adminMenuManager = adminMenuManager;
        this.webSecurityExpressionEvaluator = webSecurityExpressionEvaluator;
    }

    @ModelAttribute("adminMenuItems")
    public List<AdminMenuItem> getMenuItems(HttpServletRequest request, HttpServletResponse response) {
        List<AdminMenuItem> menuItems = adminMenuManager.getMenuItems();
        return filterInaccessibleMenuItems(menuItems, request, response);
    }

    private List<AdminMenuItem> filterInaccessibleMenuItems(List<AdminMenuItem> adminMenuItems, HttpServletRequest request, HttpServletResponse response) {
        return adminMenuItems.stream()
                .filter(item -> evaluateItem(item, request, response))
                .map(item -> filterChildren(item, request, response))
                .collect(Collectors.toList());
    }

    private boolean evaluateItem(AdminMenuItem adminMenuItem, HttpServletRequest request, HttpServletResponse response) {
        String securityExpression = adminMenuItem.getSecurityExpression();
        return securityExpression == null || webSecurityExpressionEvaluator.evaluate(securityExpression, request, response);
    }

    private AdminMenuItem filterChildren(AdminMenuItem adminMenuItem, HttpServletRequest request, HttpServletResponse response) {
        if (!adminMenuItem.getChildren().isEmpty()) {
            List<AdminMenuItem> filteredChildren = filterInaccessibleMenuItems(adminMenuItem.getChildren(), request, response);
            if (adminMenuItem.getChildren().size() != filteredChildren.size()) {
                return AdminMenuItem.fromMenuItem(adminMenuItem).setChildren(filteredChildren).build();
            }
        }
        return adminMenuItem;
    }
}
