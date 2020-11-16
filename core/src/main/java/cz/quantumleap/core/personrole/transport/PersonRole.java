package cz.quantumleap.core.personrole.transport;

import cz.quantumleap.core.data.transport.Lookup;
import cz.quantumleap.core.tables.PersonTable;
import cz.quantumleap.core.tables.RoleTable;

public class PersonRole {

    private Long id;
    private Lookup<PersonTable> personId = new Lookup<>();
    private Lookup<RoleTable> roleId = new Lookup<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
