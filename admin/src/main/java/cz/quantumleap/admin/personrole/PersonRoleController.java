package cz.quantumleap.admin.personrole;

import cz.quantumleap.admin.AdminController;
import cz.quantumleap.admin.menu.AdminMenuManager;
import cz.quantumleap.admin.person.PersonController;
import cz.quantumleap.core.data.LookupDao;
import cz.quantumleap.core.data.LookupDaoManager;
import cz.quantumleap.core.data.transport.Lookup;
import cz.quantumleap.core.personrole.transport.PersonRole;
import cz.quantumleap.core.security.WebSecurityExpressionEvaluator;
import cz.quantumleap.core.web.DefaultDetailController;
import cz.quantumleap.core.web.DetailController;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class PersonRoleController extends AdminController {

    public static final String DETAIL_URL = "/person/{personId}/person-role";
    private static final String DETAIL_VIEW = "admin/person-role";

    private static final String LIST_VIEW = "admin/person-roles";
    public static final String DATABASE_TABLE_NAME_WITH_SCHEMA = "core.person-role";

    private final LookupDaoManager lookupDaoManager;
    private final PersonRoleService personRoleService;
    private final DetailController<PersonRole> detailController;

    public PersonRoleController(AdminMenuManager adminMenuManager, WebSecurityExpressionEvaluator webSecurityExpressionEvaluator, LookupDaoManager lookupDaoManager, PersonRoleService personRoleService) {
        super(adminMenuManager, webSecurityExpressionEvaluator);
        this.lookupDaoManager = lookupDaoManager;
        this.personRoleService = personRoleService;
        this.detailController = new DefaultDetailController<>(PersonRole.class, DETAIL_URL, DETAIL_VIEW, personRoleService);
    }

    @GetMapping(path = {DETAIL_URL, DETAIL_URL + "/{id}"})
    public String showPersonRole (@PathVariable long personId, @PathVariable(required = false) Long id, Model model) {
        PersonRole detail;
        if (id == null) {
            detail = new PersonRole();
            LookupDao lookupDao = lookupDaoManager.getDaoByDatabaseTableNameWithSchema(PersonController.DATABASE_TABLE_NAME_WITH_SCHEMA);
            String personLabel = lookupDao.fetchLabelById(personId);
            detail.setPersonId(new Lookup(personId, personLabel, PersonController.DATABASE_TABLE_NAME_WITH_SCHEMA));
        } else {
            detail = personRoleService.get(id);
        }
        model.addAttribute(detail);
        return DETAIL_VIEW;
    }

    @PostMapping(path = {DETAIL_URL, DETAIL_URL + "/{id}"})
    public String savePersonRole(@PathVariable long personId, @Valid PersonRole personRole, Errors errors) {
        return detailController.save(personRole, errors);
    }
}
