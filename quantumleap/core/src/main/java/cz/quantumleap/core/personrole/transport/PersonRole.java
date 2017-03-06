package cz.quantumleap.core.personrole.transport;

import cz.quantumleap.core.persistence.transport.Lookup;

public class PersonRole {

    private Lookup personId;
    private Lookup roleId;

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
