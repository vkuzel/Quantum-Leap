package cz.quantumleap.core.notification;

import cz.quantumleap.core.notification.domain.Notification;
import cz.quantumleap.core.test.CoreSpringBootTest;
import cz.quantumleap.core.test.common.CoreTestSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@CoreSpringBootTest
class NotificationManagerTest {

    private static final String NOTIFICATION_CODE1 = "NOTIFICATION_CODE1";
    private static final String NOTIFICATION_CODE2 = "NOTIFICATION_CODE2";

    @Autowired
    private CoreTestSupport testSupport;
    @Autowired
    private NotificationDao notificationDao;

    @BeforeEach
    void clearDatabase() {
        testSupport.deleteFromTable("core.notification");
        testSupport.clearDatabase();
    }

    @Test
    void createNotificationForPersonCreatesNotificationInDatabase() {
        var definitions = createNotificationDefinition(NOTIFICATION_CODE1);
        var notificationManager = new NotificationManager(notificationDao, definitions);
        var person = testSupport.createPerson();

        long id = notificationManager.createNotificationForPerson(person.getId(), NOTIFICATION_CODE1).getId();

        var notification = notificationDao.fetchById(id, Notification.class);
        assertEquals(NOTIFICATION_CODE1, notification.getCode());
        assertEquals(person.getId(), notification.getPersonId());
        assertNull(notification.getRoleId());
    }

    @Test
    void createNotificationForPersonThrowsExceptionForNonExistingCode() {
        var definitions = createNotificationDefinition(NOTIFICATION_CODE1);
        var notificationManager = new NotificationManager(notificationDao, definitions);
        var person = testSupport.createPerson();

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                notificationManager.createNotificationForPerson(person.getId(), NOTIFICATION_CODE2));
    }

    @Test
    void createNotificationForRoleCreatesNotificationInDatabase() {
        var definitions = createNotificationDefinition(NOTIFICATION_CODE1);
        var notificationManager = new NotificationManager(notificationDao, definitions);
        var role = testSupport.createRole();

        long id = notificationManager.createNotificationForRole(role.getId(), NOTIFICATION_CODE1).getId();

        var notification = notificationDao.fetchById(id, Notification.class);
        assertEquals(NOTIFICATION_CODE1, notification.getCode());
        assertEquals(role.getId(), notification.getRoleId());
        assertNull(notification.getPersonId());
    }

    @Test
    void createNotificationForAllCreatesNotificationInDatabase() {
        var definitions = createNotificationDefinition(NOTIFICATION_CODE1);
        var notificationManager = new NotificationManager(notificationDao, definitions);

        long id = notificationManager.createNotificationForAll(NOTIFICATION_CODE1).getId();

        var notification = notificationDao.fetchById(id, Notification.class);
        assertEquals(NOTIFICATION_CODE1, notification.getCode());
        assertNull(notification.getPersonId());
        assertNull(notification.getRoleId());
    }

    private List<NotificationDefinition> createNotificationDefinition(String notificationCode) {
        return Collections.singletonList(new NotificationDefinition() {
            @Override
            public String getNotificationCode() {
                return notificationCode;
            }

            @Override
            public String getMessageCode() {
                return null;
            }
        });
    }
}
