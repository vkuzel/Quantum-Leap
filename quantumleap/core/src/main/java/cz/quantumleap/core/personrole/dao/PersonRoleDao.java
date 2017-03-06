package cz.quantumleap.core.personrole.dao;

import cz.quantumleap.core.persistence.dao.DefaultDao;
import cz.quantumleap.core.persistence.dao.lookup.LookupDaoManager;
import cz.quantumleap.core.personrole.transport.PersonRole;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static cz.quantumleap.core.tables.PersonRoleTable.PERSON_ROLE;

@Repository
public class PersonRoleDao extends DefaultDao {

    protected PersonRoleDao(DSLContext dslContext, LookupDaoManager lookupDaoManager) {
        super(PERSON_ROLE, dslContext, lookupDaoManager);
    }

    public Optional<PersonRole> fetchById(long personId, long roleId) {
        return fetchByCondition(
                PERSON_ROLE.PERSON_ID.eq(personId).and(PERSON_ROLE.ROLE_ID.eq(roleId)),
                PersonRole.class
        );
    }
}
