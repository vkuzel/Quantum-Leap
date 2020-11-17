package cz.quantumleap.admin.notification;

import cz.quantumleap.core.data.entity.EntityIdentifier;
import cz.quantumleap.core.data.transport.Lookup;
import cz.quantumleap.core.data.transport.Slice;
import cz.quantumleap.core.data.transport.SliceRequest;
import cz.quantumleap.core.data.transport.Table;
import cz.quantumleap.core.data.transport.Table.Column;
import cz.quantumleap.core.notification.NotificationDao;
import cz.quantumleap.core.notification.NotificationDefinition;
import cz.quantumleap.core.notification.NotificationManager;
import cz.quantumleap.core.notification.transport.Notification;
import cz.quantumleap.core.tables.NotificationTable;
import cz.quantumleap.core.tables.PersonTable;
import org.apache.commons.lang3.Validate;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

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
        return notificationDao.getListEntityIdentifier();
    }

    @Transactional
    public List<Notification> fetchUnresolvedByPersonId(long personId) {
        List<Notification> notifications = notificationDao.fetchUnresolvedByPersonId(personId, MAX_UNRESOLVED_NOTIFICATIONS_TO_SHOW);
        notifications.forEach(notification -> notification.setDefinition(getDefinitionForNotification(notification)));
        return notifications;
    }

    @Transactional
    public Slice<Map<Column, Object>> findSlice(long personId, SliceRequest sliceRequest) {
        Slice<Map<Column, Object>> slice = notificationDao.fetchSlice(personId, sliceRequest);
        Table<Map<Column, Object>> table = slice.getTable();

        Column codeColumn = table.getColumnByName("code");
        Column messageArgumentsColumn = table.getColumnByName("message_arguments");
        Column messageColumn = new Column(String.class, "message", false, null);

        List<Object> messages = slice.getRows().stream()
                .map(m -> translateMessage(m, codeColumn, messageArgumentsColumn))
                .collect(Collectors.toList());

        Table<Map<Column, Object>> newTable = table.createBuilder().addColumn(messageColumn, messages, (row, o) -> {
            row.put(messageColumn, o);
            return row;
        }).build();
        return new Slice<>(newTable, sliceRequest, slice.canExtend());
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
        notification.setResolvedBy(Lookup.withoutLabel(personId, PersonTable.PERSON));
        notification.setResolvedAt(LocalDateTime.now());
        notificationDao.save(notification);
    }

    private String translateMessage(Map<Column, Object> row, Column codeColumn, Column messageArgumentsColumn) {
        Locale locale = LocaleContextHolder.getLocale();
        String code = (String) row.get(codeColumn);
        Object[] messageArguments = (String[]) row.get(messageArgumentsColumn);
        NotificationDefinition definition = notificationManager.getNotificationDefinitionByCode(code);
        Validate.notNull(definition, "Notification definition not found for notification code " + code);
        return messageSource.getMessage(definition.getMessageCode(), messageArguments, locale);
    }

    private NotificationDefinition getDefinitionForNotification(Notification notification) {
        String notificationCode = notification.getCode();
        return notificationManager.getNotificationDefinitionByCode(notificationCode);
    }
}
