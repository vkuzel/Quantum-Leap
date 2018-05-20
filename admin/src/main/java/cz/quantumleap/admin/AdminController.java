package cz.quantumleap.admin;

import cz.quantumleap.admin.menu.AdminMenuItem;
import cz.quantumleap.admin.menu.AdminMenuItem.State;
import cz.quantumleap.admin.menu.AdminMenuManager;
import cz.quantumleap.core.security.WebSecurityExpressionEvaluator;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

public abstract class AdminController {

    private final AdminMenuManager adminMenuManager;
    private final WebSecurityExpressionEvaluator webSecurityExpressionEvaluator;

    public AdminController(AdminMenuManager adminMenuManager, WebSecurityExpressionEvaluator webSecurityExpressionEvaluator) {
        this.adminMenuManager = adminMenuManager;
        this.webSecurityExpressionEvaluator = webSecurityExpressionEvaluator;
    }

    @ModelAttribute("adminMenuItems")
    public List<AdminMenuItem> getMenuItems(HttpServletRequest request, HttpServletResponse response) {
        List<AdminMenuItem> menuItems = adminMenuManager.getMenuItems();
        return filterInaccessibleMenuItemsAndSetState(menuItems, request, response);
    }

    private List<AdminMenuItem> filterInaccessibleMenuItemsAndSetState(List<AdminMenuItem> adminMenuItems, HttpServletRequest request, HttpServletResponse response) {
        List<AdminMenuItem> items = new ArrayList<>(adminMenuItems.size());
        for (AdminMenuItem adminMenuItem : adminMenuItems) {
            if (!canAccessMenuItem(adminMenuItem, request, response)) {
                continue;
            }

            AdminMenuItem.Builder builder = AdminMenuItem.fromMenuItem(adminMenuItem);
            if (adminMenuItem.getPaths().contains(request.getRequestURI())) {
                builder.setState(State.ACTIVE);
            }
            if (!adminMenuItem.getChildren().isEmpty()) {
                List<AdminMenuItem> children = filterInaccessibleMenuItemsAndSetState(adminMenuItem.getChildren(), request, response);
                for (AdminMenuItem child : children) {
                    if (child.getState() == State.ACTIVE) {
                        builder.setState(State.OPEN);
                        break;
                    }
                }
                builder.setChildren(children);
            }

            items.add(builder.build());
        }
        return items;
    }

    private boolean canAccessMenuItem(AdminMenuItem adminMenuItem, HttpServletRequest request, HttpServletResponse response) {
        String securityExpression = adminMenuItem.getSecurityExpression();
        return securityExpression == null || webSecurityExpressionEvaluator.evaluate(securityExpression, request, response);
    }
}
