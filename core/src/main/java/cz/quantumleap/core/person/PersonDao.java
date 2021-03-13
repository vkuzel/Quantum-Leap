package cz.quantumleap.core.person;

import cz.quantumleap.core.database.DaoStub;
import cz.quantumleap.core.database.EntityRegistry;
import cz.quantumleap.core.database.RecordAuditor;
import cz.quantumleap.core.database.entity.Entity;
import cz.quantumleap.core.person.domain.Person;
import cz.quantumleap.core.tables.PersonTable;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import static cz.quantumleap.core.database.query.QueryUtils.startsWithIgnoreCase;
import static cz.quantumleap.core.tables.PersonTable.PERSON;

@Repository
public class PersonDao extends DaoStub<PersonTable> {

    protected PersonDao(DSLContext dslContext, RecordAuditor recordAuditor, EntityRegistry entityRegistry) {
        super(createEntity(), dslContext, recordAuditor, entityRegistry);
    }

    private static Entity<PersonTable> createEntity() {
        return Entity.builder(PERSON)
                .setLookupLabelFieldBuilder(PersonDao::createLookupLabelField)
                .setWordConditionBuilder(PersonDao::createWordCondition)
                .build();
    }

    private static Field<String> createLookupLabelField(Table<?> table) {
        return DSL.coalesce(table.field(PERSON.NAME), table.field(PERSON.EMAIL));
    }

    private static Condition createWordCondition(String text) {
        return startsWithIgnoreCase(PERSON.NAME, text)
                .or(startsWithIgnoreCase(PERSON.EMAIL, text));
    }

    public Person fetchByEmail(String email) {
        return dslContext.selectFrom(PERSON)
                .where(PERSON.EMAIL.eq(email))
                .fetchAnyInto(Person.class);
    }
}
