package cz.quantumleap.admin;

import com.google.common.collect.ImmutableList;
import cz.quantumleap.admin.menu.AdminMenuItem;
import cz.quantumleap.admin.menu.AdminMenuItemDefinition;
import cz.quantumleap.admin.menu.AdminMenuManager;
import cz.quantumleap.admin.notification.NotificationService;
import cz.quantumleap.admin.person.PersonService;
import cz.quantumleap.core.security.WebSecurityExpressionEvaluator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.util.ServletRequestPathUtils;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class AdminControllerTest {

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
        doReturn("/any-path").when(httpServletRequest).getAttribute(ServletRequestPathUtils.PATH_ATTRIBUTE);
        doReturn("/any-path").when(httpServletRequest).getAttribute(UrlPathHelper.PATH_ATTRIBUTE);
        HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);

        doReturn(true).when(webSecurityExpressionEvaluator).evaluate("authorized", httpServletRequest, httpServletResponse);
        doReturn(false).when(webSecurityExpressionEvaluator).evaluate("unauthorized", httpServletRequest, httpServletResponse);

        AdminMenuItem accessibleMenuItem = createMenuItem("authorized", Collections.emptyList());
        AdminMenuItem inaccessibleMenuItem = createMenuItem("unauthorized", Collections.emptyList());
        AdminMenuItem accessibleMenuItemWithChildren = createMenuItem("authorized", ImmutableList.of(
                accessibleMenuItem,
                inaccessibleMenuItem
        ));
        List<AdminMenuItem> adminMenuItems = ImmutableList.of(
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
        List<RequestMappingInfo> requestMappingInfoList = Collections.singletonList(RequestMappingInfo.paths("path").build());
        AdminMenuItemDefinition adminMenuItemDefinition = mock(AdminMenuItemDefinition.class);
        PreAuthorize preAuthorize = mock(PreAuthorize.class);
        doReturn(securityExpression).when(preAuthorize).value();

        return new AdminMenuItem(requestMappingInfoList, adminMenuItemDefinition, preAuthorize, AdminMenuItem.State.NONE, children);
    }
}
