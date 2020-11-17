package cz.quantumleap.core.notification;

import cz.quantumleap.core.notification.transport.Notification;
import cz.quantumleap.core.person.transport.Person;
import cz.quantumleap.core.role.transport.Role;
import cz.quantumleap.core.test.CoreSpringBootTest;
import cz.quantumleap.core.test.common.CoreTestSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

@CoreSpringBootTest
class NotificationManagerTest {

    private static final String NOTIFICATION_CODE1 = "NOTIFICATION_CODE1";
    private static final String NOTIFICATION_CODE2 = "NOTIFICATION_CODE2";

    @Autowired
    private CoreTestSupport testSupport;
    @Autowired
    private NotificationDao notificationDao;

    @BeforeEach
    private void clearDatabase() {
        testSupport.deleteFromTable("core.notification");
        testSupport.clearDatabase();
    }

    @Test
    void createNotificationForPersonCreatesNotificationInDatabase() {
        // given
        List<NotificationDefinition> definitions = createNotificationDefinition(NOTIFICATION_CODE1);
        NotificationManager notificationManager = new NotificationManager(notificationDao, definitions);
        Person person = testSupport.createPerson();

        // when
        long id = notificationManager.createNotificationForPerson(person.getId(), NOTIFICATION_CODE1).getId();

        // then
        Notification notification = notificationDao.fetchById(id, Notification.class);
        Assertions.assertEquals(NOTIFICATION_CODE1, notification.getCode());
        Assertions.assertEquals(person.getId(), notification.getPersonId().getId());
        Assertions.assertTrue(notification.getRoleId().isEmpty());
    }

    @Test
    void createNotificationForPersonThrowsExceptionForNonExistingCode() {
        // given
        List<NotificationDefinition> definitions = createNotificationDefinition(NOTIFICATION_CODE1);
        NotificationManager notificationManager = new NotificationManager(notificationDao, definitions);
        Person person = testSupport.createPerson();

        // when, then
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                notificationManager.createNotificationForPerson(person.getId(), NOTIFICATION_CODE2));
    }

    @Test
    void createNotificationForRoleCreatesNotificationInDatabase() {
        // given
        List<NotificationDefinition> definitions = createNotificationDefinition(NOTIFICATION_CODE1);
        NotificationManager notificationManager = new NotificationManager(notificationDao, definitions);
        Role role = testSupport.createRole();

        // when
        long id = notificationManager.createNotificationForRole(role.getId(), NOTIFICATION_CODE1).getId();

        // then
        Notification notification = notificationDao.fetchById(id, Notification.class);
        Assertions.assertEquals(NOTIFICATION_CODE1, notification.getCode());
        Assertions.assertEquals(role.getId(), notification.getRoleId().getId());
        Assertions.assertTrue(notification.getPersonId().isEmpty());
    }

    @Test
    void createNotificationForAllCreatesNotificationInDatabase() {
        // given
        List<NotificationDefinition> definitions = createNotificationDefinition(NOTIFICATION_CODE1);
        NotificationManager notificationManager = new NotificationManager(notificationDao, definitions);

        // when
        long id = notificationManager.createNotificationForAll(NOTIFICATION_CODE1).getId();

        // then
        Notification notification = notificationDao.fetchById(id, Notification.class);
        Assertions.assertEquals(NOTIFICATION_CODE1, notification.getCode());
        Assertions.assertTrue(notification.getPersonId().isEmpty());
        Assertions.assertTrue(notification.getRoleId().isEmpty());
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
