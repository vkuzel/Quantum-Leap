package cz.quantumleap.core.person;

import cz.quantumleap.core.common.Utils;
import cz.quantumleap.core.data.DaoStub;
import cz.quantumleap.core.data.EnumManager;
import cz.quantumleap.core.data.LookupDaoManager;
import cz.quantumleap.core.data.RecordAuditor;
import cz.quantumleap.core.data.entity.Entity;
import cz.quantumleap.core.person.transport.Person;
import cz.quantumleap.core.tables.PersonTable;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import static cz.quantumleap.core.tables.PersonTable.PERSON;

@Repository
public class PersonDao extends DaoStub<PersonTable> {

    protected PersonDao(DSLContext dslContext, RecordAuditor recordAuditor) {
        super(createEntity(), dslContext, recordAuditor);
    }

    private static Entity<PersonTable> createEntity() {
        return Entity.createBuilder(PERSON).setLookupLabelField(DSL.coalesce(PERSON.NAME, PERSON.EMAIL))
                .setWordConditionBuilder(PersonDao::createWordCondition)
                .build();
    }

    private static Condition createWordCondition(String text) {
        return Utils.startsWithIgnoreCase(PERSON.NAME, text)
                .or(Utils.startsWithIgnoreCase(PERSON.EMAIL, text));
    }

    public Person fetchByEmail(String email) {
        return dslContext.selectFrom(PERSON)
                .where(PERSON.EMAIL.eq(email))
                .fetchAnyInto(Person.class);
    }
}
