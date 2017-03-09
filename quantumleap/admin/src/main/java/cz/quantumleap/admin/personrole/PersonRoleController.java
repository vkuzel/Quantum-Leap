package cz.quantumleap.admin.personrole;

import cz.quantumleap.admin.AdminController;
import cz.quantumleap.admin.menu.AdminMenuItemDefinition;
import cz.quantumleap.admin.menu.AdminMenuManager;
import cz.quantumleap.core.data.transport.SliceRequest;
import cz.quantumleap.core.personrole.transport.PersonRole;
import cz.quantumleap.core.security.WebSecurityExpressionEvaluator;
import cz.quantumleap.core.web.DefaultListController;
import cz.quantumleap.core.web.ListController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
public class PersonRoleController extends AdminController {

    private static final String DETAIL_URL = "/person-role";
    private static final String DETAIL_VIEW = "admin/person-role";

    private static final String LIST_URL = "/person-roles";
    private static final String LIST_VIEW = "admin/person-roles";
    private static final String TABLE_NAME = "person-role";

    private final PersonRoleService personRoleService;
    private final ListController listController;

    public PersonRoleController(AdminMenuManager adminMenuManager, WebSecurityExpressionEvaluator webSecurityExpressionEvaluator, PersonRoleService personRoleService) {
        super(adminMenuManager, webSecurityExpressionEvaluator);
        this.personRoleService = personRoleService;
        this.listController = new DefaultListController(TABLE_NAME, LIST_VIEW, DETAIL_URL, personRoleService);
    }

    @GetMapping(path = {DETAIL_URL, DETAIL_URL + "/{personId}/{roleId}"})
    public String showPersonRole(@PathVariable(required = false) Long personId, @PathVariable(required = false) Long roleId, Model model) {
        PersonRole personRole = personId != null && roleId != null ? personRoleService.get(personId, roleId) : new PersonRole();
        model.addAttribute(personRole);
        return DETAIL_VIEW;
    }

    @PostMapping(path = {DETAIL_URL, DETAIL_URL + "/{personId}/{roleId}"})
    public String savePersonRole(@Valid PersonRole personRole, Errors errors) {
        if (errors.hasErrors()) {
            return DETAIL_VIEW;
        }
        PersonRole saved = personRoleService.save(personRole);
        return "redirect:" + DETAIL_URL + "/" + personRole.getPersonId() + "/" + personRole.getRoleId();
    }

    @AdminMenuItemDefinition(title = "admin.menu.person-roles", fontAwesomeIcon = "fa-users")
    @GetMapping(LIST_URL)
    public String listPersonRoles(SliceRequest sliceRequest, Model model, HttpServletRequest request) {
        return listController.list(sliceRequest, model, request);
    }
}
