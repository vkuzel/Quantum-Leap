package cz.quantumleap.admin.personrole;

import cz.quantumleap.admin.AdminController;
import cz.quantumleap.admin.menu.AdminMenuItemActive;
import cz.quantumleap.admin.menu.AdminMenuManager;
import cz.quantumleap.admin.notification.NotificationService;
import cz.quantumleap.admin.person.PersonService;
import cz.quantumleap.core.personrole.transport.PersonRole;
import cz.quantumleap.core.security.WebSecurityExpressionEvaluator;
import cz.quantumleap.core.view.DefaultDetailController;
import cz.quantumleap.core.view.DetailController;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class PersonRoleController extends AdminController {

    public static final String DETAIL_URL = "/person/{personId}/person-role";
    private static final String DETAIL_VIEW = "admin/person-role";

    private final PersonRoleService personRoleService;
    private final DetailController<PersonRole> detailController;

    public PersonRoleController(AdminMenuManager adminMenuManager, PersonService personService, NotificationService notificationService, WebSecurityExpressionEvaluator webSecurityExpressionEvaluator, PersonRoleService personRoleService) {
        super(adminMenuManager, personService, notificationService, webSecurityExpressionEvaluator);
        this.personRoleService = personRoleService;
        this.detailController = new DefaultDetailController<>(PersonRole.class, personRoleService, DETAIL_URL, DETAIL_VIEW);
    }

    @AdminMenuItemActive("admin.menu.people")
    @GetMapping(path = {DETAIL_URL, DETAIL_URL + "/{id}"})
    public String showPersonRole (@PathVariable long personId, @PathVariable(required = false) Long id, Model model) {
        PersonRole detail;
        if (id == null) {
            detail = new PersonRole();
            detail.setPersonId(personId);
        } else {
            detail = personRoleService.get(id);
        }
        model.addAttribute(detail);
        return DETAIL_VIEW;
    }

    @PostMapping(path = {DETAIL_URL, DETAIL_URL + "/{id}"})
    public String savePersonRole(@PathVariable long personId, @Valid PersonRole personRole, Errors errors, Model model, RedirectAttributes redirectAttributes) {
        return detailController.save(personRole, errors, model, redirectAttributes);
    }
}
