package cz.quantumleap.core.personrole;

import cz.quantumleap.core.database.DaoStub;
import cz.quantumleap.core.database.EntityRegistry;
import cz.quantumleap.core.database.entity.Entity;
import cz.quantumleap.core.database.entity.EntityIdentifier;
import cz.quantumleap.core.tables.PersonRoleTable;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static cz.quantumleap.core.tables.PersonRoleTable.PERSON_ROLE;
import static cz.quantumleap.core.tables.PersonTable.PERSON;
import static cz.quantumleap.core.tables.RoleTable.ROLE;

@Repository
public class PersonRoleDao extends DaoStub<PersonRoleTable> {

    protected PersonRoleDao(DSLContext dslContext, EntityRegistry entityRegistry) {
        super(createEntity(), dslContext, entityRegistry);
    }

    private static Entity<PersonRoleTable> createEntity() {
        return Entity.builder(PERSON_ROLE)
                .addLookupMetaType(PERSON_ROLE.PERSON_ID, EntityIdentifier.forTable(PERSON))
                .addLookupMetaType(PERSON_ROLE.ROLE_ID, EntityIdentifier.forTable(ROLE))
                .build();
    }
}
