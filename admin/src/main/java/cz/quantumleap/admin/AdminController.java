package cz.quantumleap.admin;

import cz.quantumleap.admin.menu.AdminMenuItem;
import cz.quantumleap.admin.menu.AdminMenuItem.State;
import cz.quantumleap.admin.menu.AdminMenuManager;
import cz.quantumleap.admin.notification.NotificationService;
import cz.quantumleap.admin.person.PersonService;
import cz.quantumleap.core.notification.domain.Notification;
import cz.quantumleap.core.security.WebSecurityExpressionEvaluator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.ArrayList;
import java.util.List;

public abstract class AdminController {

    private final AdminMenuManager adminMenuManager;
    private final PersonService personService;
    private final NotificationService notificationService;
    private final WebSecurityExpressionEvaluator webSecurityExpressionEvaluator;

    public AdminController(AdminMenuManager adminMenuManager, PersonService personService, NotificationService notificationService, WebSecurityExpressionEvaluator webSecurityExpressionEvaluator) {
        this.adminMenuManager = adminMenuManager;
        this.personService = personService;
        this.notificationService = notificationService;
        this.webSecurityExpressionEvaluator = webSecurityExpressionEvaluator;
    }

    @ModelAttribute("adminMenuItems")
    public List<AdminMenuItem> getMenuItems(HttpServletRequest request, HttpServletResponse response) {
        var menuItems = adminMenuManager.getMenuItems();
        return filterInaccessibleMenuItemsAndSetState(menuItems, request, response);
    }

    @ModelAttribute("unresolvedNotifications")
    public List<Notification> getUnresolvedNotifications(Authentication authentication) {
        var person = personService.fetchByAuthentication(authentication);
        return notificationService.fetchUnresolvedByPersonId(person.getId());
    }

    private List<AdminMenuItem> filterInaccessibleMenuItemsAndSetState(List<AdminMenuItem> adminMenuItems, HttpServletRequest request, HttpServletResponse response) {
        List<AdminMenuItem> items = new ArrayList<>(adminMenuItems.size());
        for (var adminMenuItem : adminMenuItems) {
            if (!canAccessMenuItem(adminMenuItem, request, response)) {
                continue;
            }

            var builder = AdminMenuItem.fromMenuItem(adminMenuItem);
            if (adminMenuItem.matchesRequest(request)) {
                builder.setState(State.ACTIVE);
            }
            if (!adminMenuItem.getChildren().isEmpty()) {
                var children = filterInaccessibleMenuItemsAndSetState(adminMenuItem.getChildren(), request, response);
                for (var child : children) {
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
        var securityExpression = adminMenuItem.getSecurityExpression();
        return securityExpression == null || webSecurityExpressionEvaluator.evaluate(securityExpression, request, response);
    }
}
