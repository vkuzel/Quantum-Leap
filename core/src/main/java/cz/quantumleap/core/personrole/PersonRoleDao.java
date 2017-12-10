package cz.quantumleap.core.personrole;

import cz.quantumleap.core.data.DaoStub;
import cz.quantumleap.core.data.EnumManager;
import cz.quantumleap.core.data.LookupDaoManager;
import cz.quantumleap.core.data.RecordAuditor;
import cz.quantumleap.core.tables.PersonRoleTable;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static cz.quantumleap.core.tables.PersonRoleTable.PERSON_ROLE;

@Repository
public class PersonRoleDao extends DaoStub<PersonRoleTable> {

    protected PersonRoleDao(DSLContext dslContext, LookupDaoManager lookupDaoManager, EnumManager enumManager, RecordAuditor recordAuditor) {
        super(PERSON_ROLE, null, s -> null, dslContext, lookupDaoManager, enumManager, recordAuditor);
    }
}
