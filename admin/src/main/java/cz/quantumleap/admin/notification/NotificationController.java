package cz.quantumleap.admin.notification;

import cz.quantumleap.admin.AdminController;
import cz.quantumleap.admin.menu.AdminMenuManager;
import cz.quantumleap.admin.notification.transport.Notification;
import cz.quantumleap.admin.person.PersonService;
import cz.quantumleap.core.common.Utils;
import cz.quantumleap.core.data.transport.Slice;
import cz.quantumleap.core.data.transport.SliceRequest;
import cz.quantumleap.core.person.transport.Person;
import cz.quantumleap.core.security.WebSecurityExpressionEvaluator;
import cz.quantumleap.core.web.DefaultListController;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class NotificationController extends AdminController {

    private static final String DETAIL_URL = "/notification";
    private static final String DETAIL_VIEW = "admin/notification";

    private static final String LIST_URL = "/notifications";
    private static final String LIST_VIEW = "admin/notifications";
    private static final String AJAX_LIST_VIEW = "admin/components/table";

    private final NotificationService notificationService;
    private final PersonService personService;

    public NotificationController(AdminMenuManager adminMenuManager, PersonService personService, NotificationService notificationService, WebSecurityExpressionEvaluator webSecurityExpressionEvaluator) {
        super(adminMenuManager, personService, notificationService, webSecurityExpressionEvaluator);
        this.notificationService = notificationService;
        this.personService = personService;
    }

    @GetMapping(path = DETAIL_URL + "/{id}")
    public String showNotification(@PathVariable long id, Model model, Authentication authentication) {
        Person person = personService.fetchByAuthentication(authentication);
        Notification notification = notificationService.get(person.getId(), id);
        model.addAttribute(notification);
        return DETAIL_VIEW;
    }

    @PostMapping(params = "resolve", path = DETAIL_URL + "/{id}")
    public String resolveNotification(@PathVariable long id, Authentication authentication) {
        Person person = personService.fetchByAuthentication(authentication);
        notificationService.resolve(person.getId(), id);
        return "redirect:" + DETAIL_URL + "/" + id;
    }

    @GetMapping(LIST_URL)
    public String showNotifications(SliceRequest sliceRequest, Model model, HttpServletRequest request, Authentication authentication) {
        Person person = personService.fetchByAuthentication(authentication);
        Slice slice = notificationService.findSlice(person.getId(), sliceRequest);
        model.addAttribute(DefaultListController.TABLE_SLICE_MODEL_ATTRIBUTE_NAME, slice);
        model.addAttribute(DefaultListController.ENTITY_IDENTIFIER_MODEL_ATTRIBUTE_NAME, notificationService.getListEntityIdentifier().toString());
        model.addAttribute(DefaultListController.DETAIL_URL_MODEL_ATTRIBUTE_NAME, DETAIL_URL);

        return Utils.isAjaxRequest(request) ? AJAX_LIST_VIEW : LIST_VIEW;
    }
}
