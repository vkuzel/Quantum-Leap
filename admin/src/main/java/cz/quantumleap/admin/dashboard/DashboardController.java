package cz.quantumleap.admin.dashboard;

import cz.quantumleap.admin.AdminController;
import cz.quantumleap.admin.dashboard.DashboardWidget.Position;
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
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNullElseGet;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class DashboardController extends AdminController {

    private final List<DashboardWidget> dashboardWidgets;

    public DashboardController(
            AdminMenuManager adminMenuManager,
            PersonService personService,
            NotificationService notificationService,
            WebSecurityExpressionEvaluator webSecurityExpressionEvaluator,
            @Autowired(required = false) List<DashboardWidget> dashboardWidgets
    ) {
        super(adminMenuManager, personService, notificationService, webSecurityExpressionEvaluator);
        this.dashboardWidgets = requireNonNullElseGet(dashboardWidgets, List::of);
    }

    @AdminMenuItemDefinition(title = "admin.menu.dashboard", fontAwesomeIcon = "fas fa-tachometer-alt", priority = Integer.MAX_VALUE)
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Map<Position, List<DashboardWidget>> dashboardWidgetsMap = dashboardWidgets.stream()
                .collect(Collectors.groupingBy(DashboardWidget::getPosition));
        model.addAttribute("dashboardWidgets", dashboardWidgetsMap);
        for (DashboardWidget widget : dashboardWidgets) {
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
