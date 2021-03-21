package cz.quantumleap.admin.notification;

import cz.quantumleap.core.database.domain.FetchParams;
import cz.quantumleap.core.database.domain.TableSlice;
import cz.quantumleap.core.database.domain.TableSlice.Column;
import cz.quantumleap.core.database.entity.EntityIdentifier;
import cz.quantumleap.core.notification.NotificationDao;
import cz.quantumleap.core.notification.NotificationDefinition;
import cz.quantumleap.core.notification.NotificationManager;
import cz.quantumleap.core.notification.domain.Notification;
import cz.quantumleap.core.tables.NotificationTable;
import org.apache.commons.lang3.Validate;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class NotificationService {

    private static final int MAX_UNRESOLVED_NOTIFICATIONS_TO_SHOW = 10;

    private final MessageSource messageSource;
    private final NotificationDao notificationDao;
    private final NotificationManager notificationManager;

    public NotificationService(MessageSource messageSource, NotificationDao notificationDao, NotificationManager notificationManager) {
        this.messageSource = messageSource;
        this.notificationDao = notificationDao;
        this.notificationManager = notificationManager;
    }

    public EntityIdentifier<NotificationTable> getListEntityIdentifier() {
        return notificationDao.getListEntity().getIdentifier();
    }

    @Transactional
    public List<Notification> fetchUnresolvedByPersonId(long personId) {
        List<Notification> notifications = notificationDao.fetchUnresolvedByPersonId(personId, MAX_UNRESOLVED_NOTIFICATIONS_TO_SHOW);
        notifications.forEach(notification -> notification.setDefinition(getDefinitionForNotification(notification)));
        return notifications;
    }

    @Transactional
    public TableSlice findSlice(long personId, FetchParams fetchParams) {
        Locale locale = LocaleContextHolder.getLocale();
        TableSlice slice = notificationDao.fetchSlice(personId, fetchParams);

        Column codeColumn = slice.getColumnByName("code");
        Column messageArgumentsColumn = slice.getColumnByName("message_arguments");
        Column messageColumn = new Column(String.class, "message", false, null);

        List<Object> messages = new ArrayList<>();
        for (List<Object> row : slice) {
            String code = (String) slice.getValue(codeColumn, row);
            Object[] messageArguments = (Object[]) slice.getValue(messageArgumentsColumn, row);

            NotificationDefinition definition = notificationManager.getNotificationDefinitionByCode(code);
            Validate.notNull(definition, "Notification definition not found for notification code " + code);
            String message = messageSource.getMessage(definition.getMessageCode(), messageArguments, locale);
            messages.add(message);
        }

        return slice.builder().addColumn(messageColumn, messages, (row, o) -> {
            row.add(o);
            return row;
        }).build();
    }

    @Transactional
    public Notification get(long personId, long id) {
        Notification notification = notificationDao.fetch(personId, id);
        notification.setDefinition(getDefinitionForNotification(notification));
        return notification;
    }

    @Transactional
    public void resolve(long personId, long id) {
        Notification notification = get(personId, id);
        Validate.isTrue(notification.getResolvedAt() == null);
        notification.setResolvedBy(personId);
        notification.setResolvedAt(LocalDateTime.now());
        notificationDao.save(notification);
    }

    private NotificationDefinition getDefinitionForNotification(Notification notification) {
        String notificationCode = notification.getCode();
        return notificationManager.getNotificationDefinitionByCode(notificationCode);
    }
}
