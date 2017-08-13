package cz.quantumleap.admin.role;

import cz.quantumleap.admin.AdminController;
import cz.quantumleap.admin.menu.AdminMenuItemDefinition;
import cz.quantumleap.admin.menu.AdminMenuManager;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class RoleController extends AdminController implements LookupController {

    private static final String DETAIL_URL = "/role";
    private static final String DETAIL_VIEW = "admin/role";

    private static final String LIST_URL = "/roles";
    private static final String LIST_VIEW = "admin/roles";
    private static final String TABLE_NAME = "roles";

    private static final String DATABASE_TABLE_NAME = "core.role";
    private static final String LOOKUP_LABEL_URL = "/role-lookup-label";
    private static final String LOOKUP_LABELS_URL = "/roles-lookup-labels";
    private static final String LOOKUP_LIST_URL = "/roles-lookup";

    private final DetailController<Role> detailController;
    private final ListController listController;
    private final LookupController lookupController;

    public RoleController(AdminMenuManager adminMenuManager, WebSecurityExpressionEvaluator webSecurityExpressionEvaluator, RoleService roleService) {
        super(adminMenuManager, webSecurityExpressionEvaluator);
        this.detailController = new DefaultDetailController<>(Role.class, DETAIL_URL, DETAIL_VIEW, roleService);
        this.listController = new DefaultListController(TABLE_NAME, LIST_VIEW, DETAIL_URL, roleService);
        this.lookupController = new DefaultLookupController(TABLE_NAME, DATABASE_TABLE_NAME, DETAIL_URL, LOOKUP_LABEL_URL, LOOKUP_LABELS_URL, LOOKUP_LIST_URL, roleService);
    }

    @GetMapping(path = {DETAIL_URL, DETAIL_URL + "/{id}"})
    public String showRole(@PathVariable(required = false) Long id, Model model) {
        return detailController.show(id, model);
    }

    @PostMapping(path = {DETAIL_URL, DETAIL_URL + "/{id}"})
    public String saveRole(@Valid Role role, Errors errors) {
        return detailController.save(role, errors);
    }

    @AdminMenuItemDefinition(title = "admin.menu.roles", fontAwesomeIcon = "fa-id-card")
    @GetMapping(LIST_URL)
    public String showRoles(SliceRequest sliceRequest, Model model, HttpServletRequest request) {
        return listController.list(sliceRequest, model, request);
    }

    @Override
    public String supportedDatabaseTableNameWithSchema() {
        return lookupController.supportedDatabaseTableNameWithSchema();
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
    public String findLookupLabels(String filter, Model model, HttpServletRequest request) {
        return lookupController.findLookupLabels(filter, model, request);
    }

    @GetMapping(LOOKUP_LIST_URL)
    @Override
    public String lookupList(SliceRequest sliceRequest, Model model, HttpServletRequest request) {
        return lookupController.lookupList(sliceRequest, model, request);
    }
}
