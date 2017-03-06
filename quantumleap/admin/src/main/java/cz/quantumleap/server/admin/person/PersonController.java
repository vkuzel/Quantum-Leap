package cz.quantumleap.server.admin.person;

import cz.quantumleap.core.persistence.transport.Slice;
import cz.quantumleap.core.persistence.transport.SliceRequest;
import cz.quantumleap.core.person.transport.Person;
import cz.quantumleap.server.admin.AdminController;
import cz.quantumleap.server.admin.menu.AdminMenuItemDefinition;
import cz.quantumleap.server.admin.menu.AdminMenuManager;
import cz.quantumleap.server.security.WebSecurityExpressionEvaluator;
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
public class PersonController extends AdminController {

    private static final String TABLE_SLICE_MODEL_ATTRIBUTE_NAME = "tableSlice";
    private static final String TABLE_NAME_MODEL_ATTRIBUTE_NAME = "tableName";
    private static final String DETAIL_URL_MODEL_ATTRIBUTE_NAME = "detailUrl";

    private static final String TABLE_NAME = "people";
    private static final String DETAIL_URL = "/person/";

    private final PersonService personService;

    public PersonController(
            AdminMenuManager adminMenuManager,
            WebSecurityExpressionEvaluator webSecurityExpressionEvaluator,
            PersonService personService
    ) {
        super(adminMenuManager, webSecurityExpressionEvaluator);
        this.personService = personService;
    }

    @AdminMenuItemDefinition(title = "admin.menu.people", fontAwesomeIcon = "fa-user")
    @GetMapping("/people")
    public String showPeople(SliceRequest sliceRequest, Model model, HttpServletRequest request) {
        Slice people = personService.findPeople(sliceRequest);
        model.addAttribute(TABLE_SLICE_MODEL_ATTRIBUTE_NAME, people);
        model.addAttribute(TABLE_NAME_MODEL_ATTRIBUTE_NAME, TABLE_NAME);
        model.addAttribute(DETAIL_URL_MODEL_ATTRIBUTE_NAME, DETAIL_URL);

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "admin/components/table";
        }
        return "admin/people";
    }

    @GetMapping(path = {"/person", "/person/{id}"})
    public String showPerson(@PathVariable(required = false) Long id, Model model) {
        Person person = id != null ? personService.getPerson(id) : new Person();
        model.addAttribute("person", person);
        return "admin/person";
    }

    @PostMapping("/person")
    public String savePerson(@Valid Person person, Errors errors) {
        // TODO At this point I shouldn't provide Person with setName method if I don't want to change the value!
        if (errors.hasErrors()) {
            return "admin/person";
        }
        long id = personService.savePerson(person).getId();
        return "redirect:person/" + id;
    }
}
