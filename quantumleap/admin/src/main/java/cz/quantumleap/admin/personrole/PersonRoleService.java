package cz.quantumleap.admin.personrole;

import cz.quantumleap.core.common.NotFoundException;
import cz.quantumleap.core.persistence.transport.Slice;
import cz.quantumleap.core.persistence.transport.SliceRequest;
import cz.quantumleap.core.personrole.PersonRoleDao;
import cz.quantumleap.core.personrole.transport.PersonRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PersonRoleService {

    private final PersonRoleDao personRoleDao;

    public PersonRoleService(PersonRoleDao personRoleDao) {
        this.personRoleDao = personRoleDao;
    }

    public Slice findPersonRoles(SliceRequest sliceRequest) {
        return personRoleDao.fetchSlice(sliceRequest);
    }

    public PersonRole getPersonRole(long personId, long roleId) {
        return personRoleDao.fetchById(personId, roleId).orElseThrow(() -> new NotFoundException(personId));
    }

    @Transactional
    public PersonRole savePersonRole(PersonRole personRole) {
        return personRoleDao.save(personRole);
    }
}
