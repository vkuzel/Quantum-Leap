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

    private static final String LAST_ACTIVE_PATH = "lastActiveItem";

    private final AdminMenuManager adminMenuManager;
    private final WebSecurityExpressionEvaluator webSecurityExpressionEvaluator;

    public AdminController(AdminMenuManager adminMenuManager, WebSecurityExpressionEvaluator webSecurityExpressionEvaluator) {
        this.adminMenuManager = adminMenuManager;
        this.webSecurityExpressionEvaluator = webSecurityExpressionEvaluator;
    }

    @ModelAttribute("adminMenuItems")
    public List<AdminMenuItem> getMenuItems(HttpServletRequest request, HttpServletResponse response) {
        List<AdminMenuItem> menuItems = adminMenuManager.getMenuItems();
        List<AdminMenuItem> adminMenuItems = filterInaccessibleMenuItemsAndSetState(menuItems, request, response);
        if (canFixActiveMenuItem(adminMenuItems, request)) {
            fixActiveMenuItem(adminMenuItems, request);
        }
        return adminMenuItems;
    }

    private List<AdminMenuItem> filterInaccessibleMenuItemsAndSetState(List<AdminMenuItem> adminMenuItems, HttpServletRequest request, HttpServletResponse response) {
        List<AdminMenuItem> items = new ArrayList<>(adminMenuItems.size());
        for (AdminMenuItem adminMenuItem : adminMenuItems) {
            if (!evaluateItem(adminMenuItem, request, response)) {
                continue;
            }

            AdminMenuItem.Builder builder = AdminMenuItem.fromMenuItem(adminMenuItem);
            if (adminMenuItem.getPaths().contains(request.getRequestURI())) {
                builder.setState(State.ACTIVE);
                request.getSession().setAttribute(LAST_ACTIVE_PATH, request.getRequestURI());
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

    private boolean canFixActiveMenuItem(List<AdminMenuItem> adminMenuItems, HttpServletRequest request) {
        Object lastActivePath = request.getSession().getAttribute(LAST_ACTIVE_PATH);
        if (lastActivePath == null) {
            return false;
        }

        for (AdminMenuItem adminMenuItem : adminMenuItems) {
            if (adminMenuItem.getState() == State.ACTIVE || adminMenuItem.getState() == State.OPEN) {
                return false;
            }
        }

        return true;
    }

    // TODO I can move this to Javascript sessionStorage...
    private boolean fixActiveMenuItem(List<AdminMenuItem> adminMenuItems, HttpServletRequest request) {
        Object lastActivePath = request.getSession().getAttribute(LAST_ACTIVE_PATH);
        for (AdminMenuItem adminMenuItem : adminMenuItems) {
            if (adminMenuItem.getPaths().contains(lastActivePath)) {
                int index = adminMenuItems.indexOf(adminMenuItem);
                adminMenuItems.set(index, AdminMenuItem.fromMenuItem(adminMenuItem).setState(State.ACTIVE).build());
                return true;
            } else if (!adminMenuItem.getChildren().isEmpty()) {
                if (fixActiveMenuItem(adminMenuItem.getChildren(), request)) {
                    int index = adminMenuItems.indexOf(adminMenuItem);
                    adminMenuItems.set(index, AdminMenuItem.fromMenuItem(adminMenuItem).setState(State.OPEN).build());
                    return true;
                }
            }
        }
        return false;
    }

    private boolean evaluateItem(AdminMenuItem adminMenuItem, HttpServletRequest request, HttpServletResponse response) {
        String securityExpression = adminMenuItem.getSecurityExpression();
        return securityExpression == null || webSecurityExpressionEvaluator.evaluate(securityExpression, request, response);
    }
}
