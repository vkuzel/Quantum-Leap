package cz.quantumleap.admin.person;

import cz.quantumleap.admin.AdminController;
import cz.quantumleap.admin.menu.AdminMenuItemActive;
import cz.quantumleap.admin.menu.AdminMenuItemDefinition;
import cz.quantumleap.admin.menu.AdminMenuManager;
import cz.quantumleap.admin.notification.NotificationService;
import cz.quantumleap.admin.personrole.PersonRoleController;
import cz.quantumleap.admin.personrole.PersonRoleService;
import cz.quantumleap.core.common.Utils;
import cz.quantumleap.core.data.entity.EntityIdentifier;
import cz.quantumleap.core.data.transport.Slice;
import cz.quantumleap.core.data.transport.SliceRequest;
import cz.quantumleap.core.person.transport.Person;
import cz.quantumleap.core.security.WebSecurityExpressionEvaluator;
import cz.quantumleap.core.session.SessionService;
import cz.quantumleap.core.session.transport.SessionDetail;
import cz.quantumleap.core.web.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

import static cz.quantumleap.core.tables.PersonTable.PERSON;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class PersonController extends AdminController implements LookupController {

    private static final String DETAIL_URL = "/person";
    private static final String DETAIL_VIEW = "admin/person";
    private static final String SESSIONS_VIEW = DETAIL_VIEW + "::#sessions";

    private static final String LIST_URL = "/people";
    private static final String LIST_VIEW = "admin/people";
    private static final String AJAX_LIST_VIEW = "admin/components/table";
    public static final EntityIdentifier ENTITY_IDENTIFIER = EntityIdentifier.forTable(PERSON);

    private static final String LOOKUP_LABEL_URL = "/person-lookup-label";
    private static final String LOOKUP_LABELS_URL = "/people-lookup-labels";
    private static final String LOOKUP_LIST_URL = "/people-lookup";

    private final DetailController<Person> detailController;
    private final ListController listController;
    private final LookupController lookupController;
    private final PersonService personService;
    private final PersonRoleService personRoleService;
    private final SessionService sessionService;

    public PersonController(AdminMenuManager adminMenuManager, WebSecurityExpressionEvaluator webSecurityExpressionEvaluator, PersonService personService, NotificationService notificationService, PersonRoleService personRoleService, SessionService sessionService) {
        super(adminMenuManager, personService, notificationService, webSecurityExpressionEvaluator);
        this.detailController = new DefaultDetailController<>(Person.class, DETAIL_URL, DETAIL_VIEW, personService);
        this.listController = new DefaultListController(ENTITY_IDENTIFIER, LIST_VIEW, AJAX_LIST_VIEW, DETAIL_URL, personService);
        this.lookupController = new DefaultLookupController(ENTITY_IDENTIFIER, DETAIL_URL, LOOKUP_LABEL_URL, LOOKUP_LABELS_URL, LOOKUP_LIST_URL, personService);
        this.personService = personService;
        this.personRoleService = personRoleService;
        this.sessionService = sessionService;
    }

    @AdminMenuItemActive("admin.menu.people")
    @GetMapping(path = {DETAIL_URL, DETAIL_URL + "/{id}"})
    public String showPerson(@PathVariable(required = false) Long id, @Qualifier("personRole") SliceRequest personRoleSliceRequest, Model model) {
        return detailController.show(id, model, (p) -> {
            addPersonRoleTableAttributes(p, personRoleSliceRequest, model);
            return DETAIL_VIEW;
        });
    }

    @PostMapping(params = "save", path = {DETAIL_URL, DETAIL_URL + "/{id}"})
    public String savePerson(@Valid Person person, Errors errors, @Qualifier("personRole") SliceRequest personRoleSliceRequest, Model model, RedirectAttributes redirectAttributes) {
        String view = detailController.save(person, errors, model, redirectAttributes);
        if (errors.hasErrors() && person.getId() != null) {
            addPersonRoleTableAttributes(person, personRoleSliceRequest, model);
        }
        return view;
    }

    @PostMapping(params = "invalidateSession", path = {DETAIL_URL, DETAIL_URL + "/{id}"})
    public String invalidateSession(Person person, @Qualifier("personRole") SliceRequest personRoleSliceRequest, HttpServletRequest request, Model model, @RequestParam("invalidateSession") String sessionId) {
        sessionService.invalidate(sessionId);
        model.addAttribute(person);
        addPersonRoleTableAttributes(person, personRoleSliceRequest, model);
        return Utils.isAjaxRequest(request) ? SESSIONS_VIEW : DETAIL_VIEW;
    }

    private void addPersonRoleTableAttributes(Person person, SliceRequest personRoleSliceRequest, Model model) {
        if (person == null || person.getId() == null) {
            return;
        }

        personRoleSliceRequest.getFilter().put("person_id", person.getId());
        Slice slice = personRoleService.findSlice(personRoleSliceRequest);
        model.addAttribute("personRoleTableSlice", slice);
        model.addAttribute("personRoleEntityIdentifier", PersonRoleController.ENTITY_IDENTIFIER.toString());
        model.addAttribute("personRoleDetailUrl", PersonRoleController.DETAIL_URL.replace("{personId}", String.valueOf(person.getId())));
        List<SessionDetail> sessions = sessionService.fetchListByEmail(person.getEmail());
        model.addAttribute("sessions", sessions);
    }

    @AdminMenuItemDefinition(title = "admin.menu.people", fontAwesomeIcon = "fas fa-users")
    @GetMapping(LIST_URL)
    public String showPeople(SliceRequest sliceRequest, Model model, HttpServletRequest request) {
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
