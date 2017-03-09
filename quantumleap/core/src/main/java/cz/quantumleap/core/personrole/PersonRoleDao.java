package cz.quantumleap.core.personrole;

import cz.quantumleap.core.data.DaoStub;
import cz.quantumleap.core.data.LookupDaoManager;
import cz.quantumleap.core.data.RecordAuditor;
import cz.quantumleap.core.personrole.transport.PersonRole;
import cz.quantumleap.core.tables.PersonRoleTable;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static cz.quantumleap.core.tables.PersonRoleTable.PERSON_ROLE;

@Repository
public class PersonRoleDao extends DaoStub<PersonRoleTable> {

    protected PersonRoleDao(DSLContext dslContext, LookupDaoManager lookupDaoManager, RecordAuditor recordAuditor) {
        super(PERSON_ROLE, dslContext, lookupDaoManager, recordAuditor);
    }

    public Optional<PersonRole> fetchById(long personId, long roleId) {
        return fetchByCondition(
                PERSON_ROLE.PERSON_ID.eq(personId).and(PERSON_ROLE.ROLE_ID.eq(roleId)),
                PersonRole.class
        );
    }
}
