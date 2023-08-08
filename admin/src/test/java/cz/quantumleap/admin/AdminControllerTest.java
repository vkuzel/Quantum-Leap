package cz.quantumleap.admin;

import cz.quantumleap.admin.menu.AdminMenuItem;
import cz.quantumleap.admin.menu.AdminMenuItemDefinition;
import cz.quantumleap.admin.menu.AdminMenuManager;
import cz.quantumleap.admin.notification.NotificationService;
import cz.quantumleap.admin.person.PersonService;
import cz.quantumleap.core.security.WebSecurityExpressionEvaluator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.server.RequestPath;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.util.ServletRequestPathUtils;
import org.springframework.web.util.UrlPathHelper;

import java.util.Collections;
import java.util.List;

import static cz.quantumleap.core.view.WebUtils.requestMappingInfoBuilder;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class AdminControllerTest {

    private static final String ANY_PATH = "/any-path";
    private static final RequestPath ANY_REQUEST_PATH = RequestPath.parse(ANY_PATH, "/");

    @Mock
    private AdminMenuManager adminMenuManager;
    @Mock
    private PersonService personService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private WebSecurityExpressionEvaluator webSecurityExpressionEvaluator;

    @Test
    public void onlyAccessibleMenuItemsAreReturned() {
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        doReturn(ANY_REQUEST_PATH).when(httpServletRequest).getAttribute(ServletRequestPathUtils.PATH_ATTRIBUTE);
        doReturn(ANY_PATH).when(httpServletRequest).getAttribute(UrlPathHelper.PATH_ATTRIBUTE);
        HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);

        doReturn(true).when(webSecurityExpressionEvaluator).evaluate("authorized", httpServletRequest, httpServletResponse);
        doReturn(false).when(webSecurityExpressionEvaluator).evaluate("unauthorized", httpServletRequest, httpServletResponse);

        AdminMenuItem accessibleMenuItem = createMenuItem("authorized", Collections.emptyList());
        AdminMenuItem inaccessibleMenuItem = createMenuItem("unauthorized", Collections.emptyList());
        AdminMenuItem accessibleMenuItemWithChildren = createMenuItem("authorized", List.of(
                accessibleMenuItem,
                inaccessibleMenuItem
        ));
        List<AdminMenuItem> adminMenuItems = List.of(
                inaccessibleMenuItem,
                accessibleMenuItemWithChildren
        );
        Mockito.doReturn(adminMenuItems).when(adminMenuManager).getMenuItems();
        AdminController controller = new AdminController(adminMenuManager, personService, notificationService, webSecurityExpressionEvaluator) {
        };

        List<AdminMenuItem> menuItems = controller.getMenuItems(httpServletRequest, httpServletResponse);

        Assertions.assertEquals(1, menuItems.size());
        AdminMenuItem menuItem = menuItems.get(0);
        Assertions.assertEquals(1, menuItem.getChildren().size());
        Assertions.assertEquals(accessibleMenuItem.getPath(), menuItem.getChildren().get(0).getPath());
    }

    private AdminMenuItem createMenuItem(String securityExpression, List<AdminMenuItem> children) {
        List<RequestMappingInfo> requestMappingInfoList = singletonList(requestMappingInfoBuilder("path").build());
        AdminMenuItemDefinition adminMenuItemDefinition = mock(AdminMenuItemDefinition.class);
        PreAuthorize preAuthorize = mock(PreAuthorize.class);
        doReturn(securityExpression).when(preAuthorize).value();

        return new AdminMenuItem(requestMappingInfoList, adminMenuItemDefinition, preAuthorize, AdminMenuItem.State.NONE, children);
    }
}
