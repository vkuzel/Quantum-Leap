package cz.quantumleap.admin.dashboard;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import cz.quantumleap.admin.AdminController;
import cz.quantumleap.admin.menu.AdminMenuItemDefinition;
import cz.quantumleap.admin.menu.AdminMenuManager;
import cz.quantumleap.admin.notification.NotificationService;
import cz.quantumleap.admin.person.PersonService;
import cz.quantumleap.core.security.WebSecurityExpressionEvaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class DashboardController extends AdminController {

    private final ListMultimap<DashboardWidget.Position, DashboardWidget> dashboardWidgets;

    public DashboardController(AdminMenuManager adminMenuManager, PersonService personService, NotificationService notificationService, WebSecurityExpressionEvaluator webSecurityExpressionEvaluator, @Autowired(required = false) List<DashboardWidget> dashboardWidgets) {
        super(adminMenuManager, personService, notificationService, webSecurityExpressionEvaluator);
        this.dashboardWidgets = dashboardWidgets != null ? Multimaps.index(dashboardWidgets, DashboardWidget::getPosition) : ImmutableListMultimap.of();
    }

    @AdminMenuItemDefinition(title = "admin.menu.dashboard", fontAwesomeIcon = "fas fa-tachometer-alt", priority = Integer.MAX_VALUE)
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("dashboardWidgets", dashboardWidgets);
        for (DashboardWidget widget : dashboardWidgets.values()) {
            widget.getModelAttributes().forEach((attributeName, attributeValue) -> {
                if (model.containsAttribute(attributeName)) {
                    throw new IllegalStateException("Dashboard widget model attribute " + attributeName + " is specified twice!");
                }
                model.addAttribute(attributeName, attributeValue);
            });
        }
        return "admin/dashboard";
    }
}
