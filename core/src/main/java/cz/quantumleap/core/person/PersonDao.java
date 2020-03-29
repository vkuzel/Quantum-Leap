package cz.quantumleap.core.person;

import cz.quantumleap.core.common.Utils;
import cz.quantumleap.core.data.DaoStub;
import cz.quantumleap.core.data.EnumManager;
import cz.quantumleap.core.data.LookupDaoManager;
import cz.quantumleap.core.data.RecordAuditor;
import cz.quantumleap.core.data.entity.Entity;
import cz.quantumleap.core.person.transport.Person;
import cz.quantumleap.core.tables.PersonTable;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static cz.quantumleap.core.tables.PersonTable.PERSON;

@Repository
public class PersonDao extends DaoStub<PersonTable> {

    protected PersonDao(DSLContext dslContext, LookupDaoManager lookupDaoManager, EnumManager enumManager, RecordAuditor recordAuditor) {
        super(createEntity(), dslContext, lookupDaoManager, enumManager, recordAuditor);
    }

    private static Entity<PersonTable> createEntity() {
        return Entity.createBuilder(PERSON).setLookupLabelField(PERSON.NAME.coalesce(PERSON.EMAIL))
                .setWordConditionBuilder(s -> Utils.startsWithIgnoreCase(PERSON.NAME, s).or(Utils.startsWithIgnoreCase(PERSON.EMAIL, s)))
                .build();
    }

    public Person fetchByEmail(String email) {
        return dslContext.selectFrom(PERSON)
                .where(PERSON.EMAIL.eq(email))
                .fetchAnyInto(Person.class);
    }
}
