package cz.quantumleap.admin.role;

import cz.quantumleap.admin.AdminController;
import cz.quantumleap.admin.menu.AdminMenuItemActive;
import cz.quantumleap.admin.menu.AdminMenuItemDefinition;
import cz.quantumleap.admin.menu.AdminMenuManager;
import cz.quantumleap.admin.notification.NotificationService;
import cz.quantumleap.admin.person.PersonService;
import cz.quantumleap.core.database.domain.FetchParams;
import cz.quantumleap.core.role.domain.Role;
import cz.quantumleap.core.security.WebSecurityExpressionEvaluator;
import cz.quantumleap.core.view.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class RoleController extends AdminController {

    private static final String DETAIL_URL = "/role";
    private static final String DETAIL_VIEW = "admin/role";

    private static final String LIST_URL = "/roles";
    private static final String LIST_VIEW = "admin/roles";
    private static final String AJAX_LIST_VIEW = "admin/components/slice";

    private static final String LOOKUP_LABEL_URL = "/role-lookup-label";
    private static final String LOOKUP_LABELS_URL = "/roles-lookup-labels";
    private static final String LOOKUP_LIST_URL = "/roles-lookup";

    private final DetailController<Role> detailController;
    private final ListController listController;

    public RoleController(AdminMenuManager adminMenuManager, PersonService personService, NotificationService notificationService, WebSecurityExpressionEvaluator webSecurityExpressionEvaluator, LookupRegistry lookupRegistry, RoleService roleService) {
        super(adminMenuManager, personService, notificationService, webSecurityExpressionEvaluator);
        this.detailController = new DefaultDetailController<>(Role.class, roleService, DETAIL_URL, DETAIL_VIEW);
        this.listController = new DefaultListController(roleService, LIST_VIEW, AJAX_LIST_VIEW, DETAIL_URL);
        LookupController lookupController = new DefaultLookupController(webSecurityExpressionEvaluator, roleService, DETAIL_URL, LOOKUP_LABEL_URL, LOOKUP_LABELS_URL, LOOKUP_LIST_URL);
        lookupRegistry.registerController(lookupController);
    }

    @AdminMenuItemActive("admin.menu.roles")
    @GetMapping(path = {DETAIL_URL, DETAIL_URL + "/{id}"})
    public String showRole(@PathVariable(required = false) Long id, Model model) {
        return detailController.show(id, model);
    }

    @PostMapping(path = {DETAIL_URL, DETAIL_URL + "/{id}"})
    public String saveRole(@Valid Role role, Errors errors, Model model, RedirectAttributes redirectAttributes) {
        return detailController.save(role, errors, model, redirectAttributes);
    }

    @AdminMenuItemDefinition(title = "admin.menu.roles", parentByTitle = "admin.menu.people", fontAwesomeIcon = "far fa-id-card")
    @GetMapping(LIST_URL)
    public String showRoles(FetchParams fetchParams, Model model, HttpServletRequest request) {
        return listController.list(fetchParams, model, request);
    }
}
