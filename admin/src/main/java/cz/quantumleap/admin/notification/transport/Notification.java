package cz.quantumleap.admin.notification.transport;

import cz.quantumleap.admin.notification.NotificationDefinition;
import cz.quantumleap.core.data.transport.Lookup;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class Notification {

    private Long id;
    private String code;
    private List<String> messageArguments;
    private Lookup personId = new Lookup();
    private Lookup roleId = new Lookup();
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    private Lookup resolvedBy = new Lookup();
    private NotificationDefinition definition;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<String> getMessageArguments() {
        return messageArguments;
    }

    public void setMessageArguments(List<String> messageArguments) {
        this.messageArguments = messageArguments;
    }

    public String getThymeleafMessageArguments() {
        return messageArguments.stream()
                .map(a -> "'" + a.replace("'", "\\'") + "'")
                .collect(Collectors.joining(","));
    }

    public Lookup getPersonId() {
        return personId;
    }

    public void setPersonId(Lookup personId) {
        this.personId = personId;
    }

    public Lookup getRoleId() {
        return roleId;
    }

    public void setRoleId(Lookup roleId) {
        this.roleId = roleId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public Lookup getResolvedBy() {
        return resolvedBy;
    }

    public void setResolvedBy(Lookup resolvedBy) {
        this.resolvedBy = resolvedBy;
    }

    public NotificationDefinition getDefinition() {
        return definition;
    }

    public void setDefinition(NotificationDefinition definition) {
        this.definition = definition;
    }
}