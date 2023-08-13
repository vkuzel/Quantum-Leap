package cz.quantumleap.admin.person;

import cz.quantumleap.admin.AdminController;
import cz.quantumleap.admin.menu.AdminMenuItemActive;
import cz.quantumleap.admin.menu.AdminMenuItemDefinition;
import cz.quantumleap.admin.menu.AdminMenuManager;
import cz.quantumleap.admin.notification.NotificationService;
import cz.quantumleap.admin.personrole.PersonRoleController;
import cz.quantumleap.admin.personrole.PersonRoleService;
import cz.quantumleap.core.common.Utils;
import cz.quantumleap.core.database.domain.FetchParams;
import cz.quantumleap.core.person.domain.Person;
import cz.quantumleap.core.security.WebSecurityExpressionEvaluator;
import cz.quantumleap.core.session.SessionService;
import cz.quantumleap.core.tables.PersonRoleTable;
import cz.quantumleap.core.view.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class PersonController extends AdminController {

    private static final String DETAIL_URL = "/person";
    private static final String DETAIL_VIEW = "admin/person";
    private static final String SESSIONS_VIEW = DETAIL_VIEW + "::#sessions";

    private static final String LIST_URL = "/people";
    private static final String LIST_VIEW = "admin/people";
    private static final String AJAX_LIST_VIEW = "admin/components/slice";

    private static final String LOOKUP_LABEL_URL = "/person-lookup-label";
    private static final String LOOKUP_LABELS_URL = "/people-lookup-labels";
    private static final String LOOKUP_LIST_URL = "/people-lookup";

    private final DetailController<Person> detailController;
    private final ListController listController;
    private final PersonRoleService personRoleService;
    private final SessionService sessionService;

    public PersonController(AdminMenuManager adminMenuManager, WebSecurityExpressionEvaluator webSecurityExpressionEvaluator, LookupRegistry lookupRegistry, PersonService personService, NotificationService notificationService, PersonRoleService personRoleService, SessionService sessionService) {
        super(adminMenuManager, personService, notificationService, webSecurityExpressionEvaluator);
        this.detailController = new DefaultDetailController<>(Person.class, personService, DETAIL_URL, DETAIL_VIEW);
        this.listController = new DefaultListController(personService, LIST_VIEW, AJAX_LIST_VIEW, DETAIL_URL);
        LookupController lookupController = new DefaultLookupController(webSecurityExpressionEvaluator, personService, DETAIL_URL, LOOKUP_LABEL_URL, LOOKUP_LABELS_URL, LOOKUP_LIST_URL);
        lookupRegistry.registerController(lookupController);
        this.personRoleService = personRoleService;
        this.sessionService = sessionService;
    }

    @AdminMenuItemActive("admin.menu.people")
    @GetMapping(path = {DETAIL_URL, DETAIL_URL + "/{id}"})
    public String showPerson(
            @PathVariable(required = false) Long id,
            @Qualifier("personRole") FetchParams personRoleFetchParams,
            HttpSession httpSession,
            Model model
    ) {
        return detailController.show(id, model, (p) -> {
            model.addAttribute("currentSessionId", httpSession.getId());
            addPersonRoleTableAttributes(p, personRoleFetchParams, model);
            return DETAIL_VIEW;
        });
    }

    @PostMapping(params = "save", path = {DETAIL_URL, DETAIL_URL + "/{id}"})
    public String savePerson(@Valid Person person, Errors errors, @Qualifier("personRole") FetchParams personRoleFetchParams, Model model, RedirectAttributes redirectAttributes) {
        var view = detailController.save(person, errors, model, redirectAttributes);
        if (errors.hasErrors() && person.getId() != null) {
            addPersonRoleTableAttributes(person, personRoleFetchParams, model);
        }
        return view;
    }

    @PostMapping(params = "invalidateSession", path = {DETAIL_URL, DETAIL_URL + "/{id}"})
    public String invalidateSession(
            Person person,
            @Qualifier("personRole") FetchParams personRoleFetchParams,
            HttpServletRequest request,
            HttpSession httpSession,
            Model model,
            @RequestParam("invalidateSession") String sessionId
    ) {
        if (httpSession.getId().equals(sessionId)) {
            throw new IllegalArgumentException("Cannot invalidate current session!");
        }
        sessionService.invalidate(sessionId);
        model.addAttribute(person);
        addPersonRoleTableAttributes(person, personRoleFetchParams, model);
        return Utils.isAjaxRequest(request) ? SESSIONS_VIEW : DETAIL_VIEW;
    }

    private void addPersonRoleTableAttributes(Person person, FetchParams personRoleFetchParams, Model model) {
        if (person == null || person.getId() == null) {
            return;
        }

        personRoleFetchParams = personRoleFetchParams.addFilter("person_id", person.getId());
        var slice = personRoleService.findSlice(personRoleFetchParams);
        var identifier = personRoleService.getDetailEntityIdentifier(PersonRoleTable.class);
        var sessions = sessionService.fetchListByEmail(person.getEmail());

        model.addAttribute("personRoleSlice", slice);
        model.addAttribute("personRoleEntityIdentifier", identifier.toString());
        model.addAttribute("personRoleDetailUrl", PersonRoleController.DETAIL_URL.replace("{personId}", String.valueOf(person.getId())));
        model.addAttribute("sessions", sessions);
    }

    @AdminMenuItemDefinition(title = "admin.menu.people", fontAwesomeIcon = "fas fa-users")
    @GetMapping(LIST_URL)
    public String showPeople(FetchParams fetchParams, Model model, HttpServletRequest request) {
        return listController.list(fetchParams, model, request);
    }
}
