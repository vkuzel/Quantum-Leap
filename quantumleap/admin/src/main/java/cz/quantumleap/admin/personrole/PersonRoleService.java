package cz.quantumleap.admin.personrole;

import cz.quantumleap.core.business.DefaultListService;
import cz.quantumleap.core.business.ListService;
import cz.quantumleap.core.data.transport.Slice;
import cz.quantumleap.core.data.transport.SliceRequest;
import cz.quantumleap.core.personrole.PersonRoleDao;
import cz.quantumleap.core.personrole.transport.PersonRole;
import org.springframework.stereotype.Service;

import static cz.quantumleap.core.tables.PersonRoleTable.PERSON_ROLE;

@Service
public class PersonRoleService implements ListService {

    private final PersonRoleDao personRoleDao;
    private final ListService listService;

    public PersonRoleService(PersonRoleDao personRoleDao) {
        this.personRoleDao = personRoleDao;
        this.listService = new DefaultListService(personRoleDao);
    }

    public PersonRole get(long personId, long roleId) {
        return personRoleDao.fetchByCondition(
                PERSON_ROLE.PERSON_ID.eq(personId).and(PERSON_ROLE.ROLE_ID.eq(roleId)),
                PersonRole.class
        ).orElseThrow(() -> new IllegalArgumentException("Person-role was not found for personId " + personId + " and roleId " + roleId));
    }

    public PersonRole save(PersonRole personRole) {
        return personRoleDao.save(personRole);
    }

    @Override
    public Slice findSlice(SliceRequest sliceRequest) {
        return listService.findSlice(sliceRequest);
    }
}
