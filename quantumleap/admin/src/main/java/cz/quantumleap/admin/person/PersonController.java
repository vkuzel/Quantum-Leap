package cz.quantumleap.admin.person;

import cz.quantumleap.admin.AdminController;
import cz.quantumleap.admin.menu.AdminMenuItemDefinition;
import cz.quantumleap.admin.menu.AdminMenuManager;
import cz.quantumleap.core.data.transport.SliceRequest;
import cz.quantumleap.core.person.transport.Person;
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
public class PersonController extends AdminController implements LookupController {

    private static final String DETAIL_URL = "/person";
    private static final String DETAIL_VIEW = "admin/person";

    private static final String LIST_URL = "/people";
    private static final String LIST_VIEW = "admin/people";
    private static final String TABLE_NAME = "people";

    private static final String DATABASE_TABLE_NAME = "core.person";
    private static final String LOOKUP_LABEL_URL = "/person-lookup-label";
    private static final String LOOKUP_LABELS_URL = "/people-lookup-labels";
    private static final String LOOKUP_LIST_URL = "/people-lookup";

    private final DetailController<Person> detailController;
    private final ListController listController;
    private final LookupController lookupController;

    public PersonController(AdminMenuManager adminMenuManager, WebSecurityExpressionEvaluator webSecurityExpressionEvaluator, PersonService personService) {
        super(adminMenuManager, webSecurityExpressionEvaluator);
        this.detailController = new DefaultDetailController<>(Person.class, DETAIL_URL, DETAIL_VIEW, personService);
        this.listController = new DefaultListController(TABLE_NAME, LIST_VIEW, DETAIL_URL, personService);
        this.lookupController = new DefaultLookupController(TABLE_NAME, DATABASE_TABLE_NAME, DETAIL_URL, LOOKUP_LABEL_URL, LOOKUP_LABELS_URL, LOOKUP_LIST_URL, personService);
    }

    @GetMapping(path = {DETAIL_URL, DETAIL_URL + "/{id}"})
    public String showPerson(@PathVariable(required = false) Long id, Model model) {
        return detailController.show(id, model);
    }

    @PostMapping(path = {DETAIL_URL, DETAIL_URL + "/{id}"})
    public String savePerson(@Valid Person person, Errors errors) {
        // TODO At this point I shouldn't provide Person with setName method if I don't want to change the value!
        return detailController.save(person, errors);
    }

    @AdminMenuItemDefinition(title = "admin.menu.people", fontAwesomeIcon = "fa-user")
    @GetMapping(LIST_URL)
    public String showPeople(SliceRequest sliceRequest, Model model, HttpServletRequest request) {
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
