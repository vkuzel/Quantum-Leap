package cz.quantumleap.core.personrole;

import cz.quantumleap.core.database.DaoStub;
import cz.quantumleap.core.database.EntityRegistry;
import cz.quantumleap.core.database.RecordAuditor;
import cz.quantumleap.core.database.entity.Entity;
import cz.quantumleap.core.tables.PersonRoleTable;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static cz.quantumleap.core.tables.PersonRoleTable.PERSON_ROLE;

@Repository
public class PersonRoleDao extends DaoStub<PersonRoleTable> {

    protected PersonRoleDao(DSLContext dslContext, RecordAuditor recordAuditor, EntityRegistry entityRegistry) {
        super(createEntity(), dslContext, recordAuditor, entityRegistry);
    }

    private static Entity<PersonRoleTable> createEntity() {
        return Entity.builder(PERSON_ROLE).build();
    }
}
