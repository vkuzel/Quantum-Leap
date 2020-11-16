package cz.quantumleap.core.notification;

public interface NotificationDefinition {

    String getNotificationCode();

    /**
     * @return Java i18n message code.
     */
    String getMessageCode();
}
