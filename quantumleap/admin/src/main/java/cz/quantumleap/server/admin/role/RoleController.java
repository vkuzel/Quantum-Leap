package cz.quantumleap.server.admin.role;

import cz.quantumleap.core.role.transport.Role;
import cz.quantumleap.server.admin.AdminController;
import cz.quantumleap.server.admin.menu.AdminMenuItemDefinition;
import cz.quantumleap.server.admin.menu.AdminMenuManager;
import cz.quantumleap.core.persistence.transport.SliceRequest;
import cz.quantumleap.core.persistence.transport.Slice;
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
public class RoleController extends AdminController {

    private static final String TABLE_SLICE_MODEL_ATTRIBUTE_NAME = "tableSlice";
    private static final String TABLE_NAME_MODEL_ATTRIBUTE_NAME = "tableName";
    private static final String DETAIL_URL_MODEL_ATTRIBUTE_NAME = "detailUrl";

    private static final String TABLE_NAME = "roles";
    private static final String DETAIL_URL = "/role/";

    private final RoleService roleService;

    public RoleController(AdminMenuManager adminMenuManager, WebSecurityExpressionEvaluator webSecurityExpressionEvaluator, RoleService roleService) {
        super(adminMenuManager, webSecurityExpressionEvaluator);
        this.roleService = roleService;
    }

    @AdminMenuItemDefinition(title = "admin.menu.roles", fontAwesomeIcon = "fa-id-card")
    @GetMapping("/roles")
    public String showRoles(SliceRequest sliceRequest, Model model, HttpServletRequest request) {
        Slice people = roleService.findRoles(sliceRequest);
        model.addAttribute(TABLE_SLICE_MODEL_ATTRIBUTE_NAME, people);
        model.addAttribute(TABLE_NAME_MODEL_ATTRIBUTE_NAME, TABLE_NAME);
        model.addAttribute(DETAIL_URL_MODEL_ATTRIBUTE_NAME, DETAIL_URL);

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "admin/components/table";
        }
        return "admin/roles";
    }

    @GetMapping(path = {"/role", "/role/{id}"})
    public String showRole(@PathVariable(required = false) Long id, Model model) {
        Role role = id != null ? roleService.getRole(id) : new Role();
        model.addAttribute(role);
        return "admin/role";
    }

    @PostMapping("/role")
    public String saveRole(@Valid Role role, Errors errors) {
        if (errors.hasErrors()) {
            return "admin/role";
        }
        long id = roleService.saveRole(role).getId();
        return "redirect:role/" + id;
    }
}
