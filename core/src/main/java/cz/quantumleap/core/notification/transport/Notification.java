package cz.quantumleap.core.notification.transport;

import cz.quantumleap.core.data.transport.Lookup;
import cz.quantumleap.core.notification.NotificationDefinition;
import cz.quantumleap.core.tables.PersonTable;
import cz.quantumleap.core.tables.RoleTable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class Notification {

    private Long id;
    private String code;
    private List<String> messageArguments;
    private Lookup<PersonTable> personId = new Lookup<>();
    private Lookup<RoleTable> roleId = new Lookup<>();
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    private Lookup<PersonTable> resolvedBy = new Lookup<>();
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

    public Lookup<PersonTable> getPersonId() {
        return personId;
    }

    public void setPersonId(Lookup<PersonTable> personId) {
        this.personId = personId;
    }

    public Lookup<RoleTable> getRoleId() {
        return roleId;
    }

    public void setRoleId(Lookup<RoleTable> roleId) {
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

    public Lookup<PersonTable> getResolvedBy() {
        return resolvedBy;
    }

    public void setResolvedBy(Lookup<PersonTable> resolvedBy) {
        this.resolvedBy = resolvedBy;
    }

    public NotificationDefinition getDefinition() {
        return definition;
    }

    public void setDefinition(NotificationDefinition definition) {
        this.definition = definition;
    }
}
