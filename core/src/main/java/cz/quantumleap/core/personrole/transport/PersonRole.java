package cz.quantumleap.core.personrole.transport;

import cz.quantumleap.core.data.transport.Lookup;

public class PersonRole {

    private Long id;
    private Lookup personId = new Lookup();
    private Lookup roleId = new Lookup();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
