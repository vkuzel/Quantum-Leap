package cz.quantumleap.admin.personrole;

import cz.quantumleap.core.business.ServiceStub;
import cz.quantumleap.core.personrole.PersonRoleDao;
import cz.quantumleap.core.personrole.transport.PersonRole;
import org.springframework.stereotype.Service;

@Service
public class PersonRoleService extends ServiceStub<PersonRole> {

    public PersonRoleService(PersonRoleDao personRoleDao) {
        super(PersonRole.class, personRoleDao);
    }
}
