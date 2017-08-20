package cz.quantumleap.admin.personrole;

import cz.quantumleap.admin.AdminController;
import cz.quantumleap.admin.menu.AdminMenuItemDefinition;
import cz.quantumleap.admin.menu.AdminMenuManager;
import cz.quantumleap.core.data.transport.SliceRequest;
import cz.quantumleap.core.personrole.transport.PersonRole;
import cz.quantumleap.core.security.WebSecurityExpressionEvaluator;
import cz.quantumleap.core.web.DefaultDetailController;
import cz.quantumleap.core.web.DefaultListController;
import cz.quantumleap.core.web.DetailController;
import cz.quantumleap.core.web.ListController;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class PersonRoleController extends AdminController {

    private static final String DETAIL_URL = "/person-role";
    private static final String DETAIL_VIEW = "admin/person-role";

    private static final String LIST_URL = "/person-roles";
    private static final String LIST_VIEW = "admin/person-roles";
    private static final String TABLE_NAME = "person-role";

    private final DetailController<PersonRole> detailController;
    private final ListController listController;

    public PersonRoleController(AdminMenuManager adminMenuManager, WebSecurityExpressionEvaluator webSecurityExpressionEvaluator, PersonRoleService personRoleService) {
        super(adminMenuManager, webSecurityExpressionEvaluator);
        this.detailController = new DefaultDetailController<>(PersonRole.class, DETAIL_URL, DETAIL_VIEW, personRoleService);
        this.listController = new DefaultListController(TABLE_NAME, LIST_VIEW, DETAIL_URL, personRoleService);
    }

    @GetMapping(path = {DETAIL_URL, DETAIL_URL + "/{id}"})
    public String showPersonRole(@PathVariable(required = false) Long id, Model model) {
        return detailController.show(id, model);
    }

    @PostMapping(path = {DETAIL_URL, DETAIL_URL + "/{id}"})
    public String savePersonRole(@Valid PersonRole personRole, Errors errors) {
        return detailController.save(personRole, errors);
    }

    @AdminMenuItemDefinition(title = "admin.menu.person-roles", parentByTitle = "admin.menu.people", fontAwesomeIcon = "fa-users")
    @GetMapping(LIST_URL)
    public String listPersonRoles(SliceRequest sliceRequest, Model model, HttpServletRequest request) {
        return listController.list(sliceRequest, model, request);
    }
}
