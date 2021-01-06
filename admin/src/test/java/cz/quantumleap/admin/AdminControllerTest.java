package cz.quantumleap.admin;

import com.google.common.collect.ImmutableList;
import cz.quantumleap.admin.menu.AdminMenuItem;
import cz.quantumleap.admin.menu.AdminMenuItemDefinition;
import cz.quantumleap.admin.menu.AdminMenuManager;
import cz.quantumleap.admin.notification.NotificationService;
import cz.quantumleap.admin.person.PersonService;
import cz.quantumleap.core.security.WebSecurityExpressionEvaluator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
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
        // given
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        doReturn("/").when(httpServletRequest).getContextPath();
        doReturn("/").when(httpServletRequest).getServletPath();
        doReturn("/path").when(httpServletRequest).getRequestURI();
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

        // when
        List<AdminMenuItem> menuItems = controller.getMenuItems(httpServletRequest, httpServletResponse);

        // then
        Assert.assertEquals(1, menuItems.size());
        AdminMenuItem menuItem = menuItems.get(0);
        Assert.assertEquals(1, menuItem.getChildren().size());
        Assert.assertEquals(accessibleMenuItem.getPath(), menuItem.getChildren().get(0).getPath());
    }

    private AdminMenuItem createMenuItem(String securityExpression, List<AdminMenuItem> children) {
        List<RequestMappingInfo> requestMappingInfoList = Collections.singletonList(RequestMappingInfo.paths("path").build());
        AdminMenuItemDefinition adminMenuItemDefinition = mock(AdminMenuItemDefinition.class);
        PreAuthorize preAuthorize = mock(PreAuthorize.class);
        doReturn(securityExpression).when(preAuthorize).value();

        return new AdminMenuItem(requestMappingInfoList, adminMenuItemDefinition, preAuthorize, AdminMenuItem.State.NONE, children);
    }
}
