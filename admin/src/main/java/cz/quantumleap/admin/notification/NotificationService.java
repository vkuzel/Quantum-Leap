package cz.quantumleap.admin.notification;

import cz.quantumleap.core.database.domain.FetchParams;
import cz.quantumleap.core.database.domain.Slice;
import cz.quantumleap.core.database.domain.Slice.Column;
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

import static java.util.Objects.requireNonNull;

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
        var notifications = notificationDao.fetchUnresolvedByPersonId(personId, MAX_UNRESOLVED_NOTIFICATIONS_TO_SHOW);
        notifications.forEach(notification -> notification.setDefinition(getDefinitionForNotification(notification)));
        return notifications;
    }

    @Transactional
    public Slice findSlice(long personId, FetchParams fetchParams) {
        var locale = LocaleContextHolder.getLocale();
        var slice = notificationDao.fetchSlice(personId, fetchParams);

        var codeColumn = slice.getColumnByName("code");
        var messageArgumentsColumn = slice.getColumnByName("message_arguments");
        var messageColumn = new Column(String.class, "message", false, null);

        List<Object> messages = new ArrayList<>();
        for (var row : slice) {
            var code = (String) slice.getValue(codeColumn, row);
            var messageArguments = (Object[]) slice.getValue(messageArgumentsColumn, row);

            var definition = notificationManager.getNotificationDefinitionByCode(code);
            requireNonNull(definition, "Notification definition not found for notification code " + code);
            var message = messageSource.getMessage(definition.getMessageCode(), messageArguments, locale);
            messages.add(message);
        }

        return slice.builder()
                .addColumn(messageColumn, messages, (row, o) -> {
                    row.add(o);
                    return row;
                })
                .removeColumn(codeColumn)
                .removeColumn(messageArgumentsColumn)
                .build();
    }

    @Transactional
    public Notification get(long personId, long id) {
        var notification = notificationDao.fetch(personId, id);
        notification.setDefinition(getDefinitionForNotification(notification));
        return notification;
    }

    @Transactional
    public void resolve(long personId, long id) {
        var notification = get(personId, id);
        Validate.isTrue(notification.getResolvedAt() == null);
        notification.setResolvedBy(personId);
        notification.setResolvedAt(LocalDateTime.now());
        notificationDao.save(notification);
    }

    private NotificationDefinition getDefinitionForNotification(Notification notification) {
        var notificationCode = notification.getCode();
        return notificationManager.getNotificationDefinitionByCode(notificationCode);
    }
}
