package cz.quantumleap.admin.notification.transport;

public interface NotificationDefinition {

    String getNotificationCode();

    /**
     * @return Java i18n message code.
     */
    String getMessageCode();
}
