package cz.quantumleap.admin.personrole;

import cz.quantumleap.admin.AdminController;
import cz.quantumleap.admin.menu.AdminMenuItemDefinition;
import cz.quantumleap.admin.menu.AdminMenuManager;
import cz.quantumleap.core.persistence.transport.Slice;
import cz.quantumleap.core.persistence.transport.SliceRequest;
import cz.quantumleap.core.personrole.transport.PersonRole;
import cz.quantumleap.core.security.WebSecurityExpressionEvaluator;
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

    private static final String TABLE_SLICE_MODEL_ATTRIBUTE_NAME = "tableSlice";
    private static final String TABLE_NAME_MODEL_ATTRIBUTE_NAME = "tableName";
    private static final String DETAIL_URL_MODEL_ATTRIBUTE_NAME = "detailUrl";

    private static final String TABLE_NAME = "person-role";
    private static final String DETAIL_URL = "/person-role/";

    private final PersonRoleService personRoleService;

    public PersonRoleController(AdminMenuManager adminMenuManager, WebSecurityExpressionEvaluator webSecurityExpressionEvaluator, PersonRoleService personRoleService) {
        super(adminMenuManager, webSecurityExpressionEvaluator);
        this.personRoleService = personRoleService;
    }

    @AdminMenuItemDefinition(title = "admin.menu.person-roles", fontAwesomeIcon = "fa-users")
    @GetMapping("/person-roles")
    public String showRoles(SliceRequest sliceRequest, Model model, HttpServletRequest request) {
        Slice personRoles = personRoleService.findPersonRoles(sliceRequest);
        model.addAttribute(TABLE_SLICE_MODEL_ATTRIBUTE_NAME, personRoles);
        model.addAttribute(TABLE_NAME_MODEL_ATTRIBUTE_NAME, TABLE_NAME);
        model.addAttribute(DETAIL_URL_MODEL_ATTRIBUTE_NAME, DETAIL_URL);

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "admin/components/table";
        }
        return "admin/person-roles";
    }

    @GetMapping(path = {"/person-role", "/person-role/{personId}/{roleId}"})
    public String showRole(@PathVariable(required = false) Long personId, @PathVariable(required = false) Long roleId, Model model) {
        PersonRole personRole = personId != null && roleId != null
                ? personRoleService.getPersonRole(personId, roleId)
                : new PersonRole();
        model.addAttribute(personRole);
        return "admin/person-role";
    }

    @PostMapping("/person-role")
    public String saveRole(@Valid PersonRole personRole, Errors errors) {
        if (errors.hasErrors()) {
            return "admin/person-role";
        }
        PersonRole saved = personRoleService.savePersonRole(personRole);
        return "redirect:person-role/" + saved.getPersonId() + "/" + saved.getRoleId();
    }
}
