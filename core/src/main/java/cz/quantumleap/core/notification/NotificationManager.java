package cz.quantumleap.core.notification;

import cz.quantumleap.core.data.transport.Lookup;
import cz.quantumleap.core.notification.transport.Notification;
import cz.quantumleap.core.tables.PersonTable;
import cz.quantumleap.core.tables.RoleTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
public class NotificationManager {

    private final NotificationDao notificationDao;
    private final Map<String, NotificationDefinition> notificationDefinitionMap;

    public NotificationManager(NotificationDao notificationDao, @Autowired(required = false) Collection<NotificationDefinition> notificationDefinitions) {
        this.notificationDao = notificationDao;
        this.notificationDefinitionMap = createNotificationDefinitionMap(notificationDefinitions);
    }

    private Map<String, NotificationDefinition> createNotificationDefinitionMap(Collection<NotificationDefinition> definitions) {
        if (definitions == null) {
            return Collections.emptyMap();
        }

        Map<String, NotificationDefinition> definitionMap = new HashMap<>(definitions.size());
        for (NotificationDefinition definition : definitions) {
            String notificationCode = definition.getNotificationCode();
            if (definitionMap.put(notificationCode, definition) != null) {
                throw new IllegalStateException("Two notification definitions with same code " + notificationCode);
            }
        }
        return definitionMap;
    }

    public NotificationDefinition getNotificationDefinitionByCode(String notificationCode) {
        NotificationDefinition notificationDefinition = notificationDefinitionMap.get(notificationCode);
        if (notificationDefinition == null) {
            throw new IllegalArgumentException("Unknown notification code " + notificationCode);
        }
        return notificationDefinition;
    }

    @Transactional
    @SuppressWarnings("unused")
    public Notification createNotificationForPerson(String notificationCode, List<String> messageArguments, long personId) {
        getNotificationDefinitionByCode(notificationCode);
        Notification notification = new Notification();
        notification.setCode(notificationCode);
        notification.setMessageArguments(messageArguments);
        notification.setPersonId(Lookup.withoutLabel(personId, PersonTable.PERSON));
        return findOrSave(notification);
    }

    @Transactional
    @SuppressWarnings("unused")
    public Notification createNotificationForRole(String notificationCode, List<String> messageArguments, long roleId) {
        getNotificationDefinitionByCode(notificationCode);
        Notification notification = new Notification();
        notification.setCode(notificationCode);
        notification.setMessageArguments(messageArguments);
        notification.setRoleId(Lookup.withoutLabel(roleId, RoleTable.ROLE));
        return findOrSave(notification);
    }

    @Transactional
    @SuppressWarnings("unused")
    public Notification createNotificationForAll(String notificationCode, List<String> messageArguments) {
        getNotificationDefinitionByCode(notificationCode);
        Notification notification = new Notification();
        notification.setCode(notificationCode);
        notification.setMessageArguments(messageArguments);
        return findOrSave(notification);
    }

    private Notification findOrSave(Notification notification) {
        Notification existing = notificationDao.fetchUnresolvedByDefinition(notification);
        if (existing != null) {
            return existing;
        } else {
            return notificationDao.save(notification);
        }
    }
}
