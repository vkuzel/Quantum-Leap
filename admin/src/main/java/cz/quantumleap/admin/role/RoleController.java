package cz.quantumleap.admin.role;

import cz.quantumleap.admin.AdminController;
import cz.quantumleap.admin.menu.AdminMenuItemActive;
import cz.quantumleap.admin.menu.AdminMenuItemDefinition;
import cz.quantumleap.admin.menu.AdminMenuManager;
import cz.quantumleap.admin.notification.NotificationService;
import cz.quantumleap.admin.person.PersonService;
import cz.quantumleap.core.data.entity.EntityIdentifier;
import cz.quantumleap.core.data.transport.SliceRequest;
import cz.quantumleap.core.role.transport.Role;
import cz.quantumleap.core.security.WebSecurityExpressionEvaluator;
import cz.quantumleap.core.web.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static cz.quantumleap.core.tables.RoleTable.ROLE;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class RoleController extends AdminController implements LookupController {

    private static final String DETAIL_URL = "/role";
    private static final String DETAIL_VIEW = "admin/role";

    private static final String LIST_URL = "/roles";
    private static final String LIST_VIEW = "admin/roles";
    private static final String AJAX_LIST_VIEW = "admin/components/table";
    private static final EntityIdentifier ENTITY_IDENTIFIER = EntityIdentifier.forTable(ROLE);

    private static final String LOOKUP_LABEL_URL = "/role-lookup-label";
    private static final String LOOKUP_LABELS_URL = "/roles-lookup-labels";
    private static final String LOOKUP_LIST_URL = "/roles-lookup";

    private final DetailController<Role> detailController;
    private final ListController listController;
    private final LookupController lookupController;

    public RoleController(AdminMenuManager adminMenuManager, PersonService personService, NotificationService notificationService, WebSecurityExpressionEvaluator webSecurityExpressionEvaluator, RoleService roleService) {
        super(adminMenuManager, personService, notificationService, webSecurityExpressionEvaluator);
        this.detailController = new DefaultDetailController<>(Role.class, DETAIL_URL, DETAIL_VIEW, roleService);
        this.listController = new DefaultListController(ENTITY_IDENTIFIER, LIST_VIEW, AJAX_LIST_VIEW, DETAIL_URL, roleService);
        this.lookupController = new DefaultLookupController(ENTITY_IDENTIFIER, DETAIL_URL, LOOKUP_LABEL_URL, LOOKUP_LABELS_URL, LOOKUP_LIST_URL, roleService);
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
    public String showRoles(SliceRequest sliceRequest, Model model, HttpServletRequest request) {
        return listController.list(sliceRequest, model, request);
    }

    @Override
    public EntityIdentifier getEntityIdentifier() {
        return lookupController.getEntityIdentifier();
    }

    @Override
    public String getDetailUrl() {
        return lookupController.getDetailUrl();
    }

    @Override
    public String getLookupLabelUrl() {
        return lookupController.getLookupLabelUrl();
    }

    @Override
    public String getLookupLabelsUrl() {
        return lookupController.getLookupLabelsUrl();
    }

    @Override
    public String getLookupListUrl() {
        return lookupController.getLookupListUrl();
    }

    @GetMapping(LOOKUP_LABEL_URL)
    @ResponseBody
    @Override
    public String resolveLookupLabel(String id) {
        return lookupController.resolveLookupLabel(id);
    }

    @GetMapping(LOOKUP_LABELS_URL)
    @Override
    public String findLookupLabels(String query, Model model, HttpServletRequest request) {
        return lookupController.findLookupLabels(query, model, request);
    }

    @GetMapping(LOOKUP_LIST_URL)
    @Override
    public String lookupList(SliceRequest sliceRequest, Model model, HttpServletRequest request) {
        return lookupController.lookupList(sliceRequest, model, request);
    }
}
