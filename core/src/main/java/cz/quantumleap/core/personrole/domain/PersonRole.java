package cz.quantumleap.core.personrole.domain;

import jakarta.validation.constraints.NotNull;

public class PersonRole {

    private Long id;
    @NotNull
    private Long personId;
    @NotNull
    private Long roleId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}
