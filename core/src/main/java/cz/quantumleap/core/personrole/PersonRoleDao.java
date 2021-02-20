package cz.quantumleap.core.personrole;

import cz.quantumleap.core.data.DaoStub;
import cz.quantumleap.core.data.EntityManager;
import cz.quantumleap.core.data.RecordAuditor;
import cz.quantumleap.core.data.entity.Entity;
import cz.quantumleap.core.tables.PersonRoleTable;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static cz.quantumleap.core.tables.PersonRoleTable.PERSON_ROLE;

@Repository
public class PersonRoleDao extends DaoStub<PersonRoleTable> {

    protected PersonRoleDao(DSLContext dslContext, RecordAuditor recordAuditor, EntityManager entityManager) {
        super(createEntity(), dslContext, recordAuditor, entityManager);
    }

    private static Entity<PersonRoleTable> createEntity() {
        return Entity.createBuilder(PERSON_ROLE).build();
    }
}
