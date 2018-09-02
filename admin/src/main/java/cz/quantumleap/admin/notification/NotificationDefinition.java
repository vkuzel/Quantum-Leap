package cz.quantumleap.admin.notification;

public interface NotificationDefinition {

    String getNotificationCode();

    /**
     * @return Java i18n message code.
     */
    String getMessageCode();
}
