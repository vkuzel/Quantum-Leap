package cz.quantumleap.core.notification;

import cz.quantumleap.core.data.TransactionExecutor;
import cz.quantumleap.core.notification.transport.Notification;
import cz.quantumleap.core.person.PersonDao;
import cz.quantumleap.core.person.transport.Person;
import cz.quantumleap.core.role.RoleDao;
import cz.quantumleap.core.role.transport.Role;
import cz.quantumleap.core.test.CoreSpringBootTest;
import cz.quantumleap.core.test.common.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

import static java.util.Collections.emptyList;

@CoreSpringBootTest
class NotificationManagerTest {

    private static final String NOTIFICATION_CODE1 = "NOTIFICATION_CODE1";
    private static final String NOTIFICATION_CODE2 = "NOTIFICATION_CODE2";

    @Autowired
    private PersonDao personDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private TransactionExecutor transactionExecutor;
    @Autowired
    private NotificationDao notificationDao;

    @BeforeEach
    private void clearDatabase() {
        transactionExecutor.execute(dslContext -> {
            TestUtils.deleteFromTable(dslContext, "core.notification");
            TestUtils.deleteFromTable(dslContext, "core.person");
            TestUtils.deleteFromTable(dslContext, "core.role");
        });
    }

    @Test
    void createNotificationForPersonCreatesNotificationInDatabase() {
        // given
        List<NotificationDefinition> definitions = createNotificationDefinition(NOTIFICATION_CODE1);
        NotificationManager notificationManager = new NotificationManager(notificationDao, definitions);
        Person person = createPerson();

        // when
        long id = notificationManager.createNotificationForPerson(NOTIFICATION_CODE1, emptyList(), person.getId()).getId();

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
        Person person = createPerson();

        // when, then
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                notificationManager.createNotificationForPerson(NOTIFICATION_CODE2, emptyList(), person.getId()));
    }

    @Test
    void createNotificationForRoleCreatesNotificationInDatabase() {
        // given
        List<NotificationDefinition> definitions = createNotificationDefinition(NOTIFICATION_CODE1);
        NotificationManager notificationManager = new NotificationManager(notificationDao, definitions);
        Role role = createRole();

        // when
        long id = notificationManager.createNotificationForRole(NOTIFICATION_CODE1, emptyList(), role.getId()).getId();

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
        long id = notificationManager.createNotificationForAll(NOTIFICATION_CODE1, emptyList()).getId();

        // then
        Notification notification = notificationDao.fetchById(id, Notification.class);
        Assertions.assertEquals(NOTIFICATION_CODE1, notification.getCode());
        Assertions.assertTrue(notification.getPersonId().isEmpty());
        Assertions.assertTrue(notification.getRoleId().isEmpty());
    }

    private Person createPerson() {
        return transactionExecutor.execute(() -> {
            Person person = new Person();
            person.setEmail("test@email.cx");
            return personDao.save(person);
        });
    }

    private Role createRole() {
        return transactionExecutor.execute(() -> {
            Role role = new Role();
            role.setName("Test role");
            return roleDao.save(role);
        });
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
