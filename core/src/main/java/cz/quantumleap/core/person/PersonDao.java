package cz.quantumleap.core.person;

import cz.quantumleap.core.common.Utils;
import cz.quantumleap.core.data.DaoStub;
import cz.quantumleap.core.data.EntityRegistry;
import cz.quantumleap.core.data.RecordAuditor;
import cz.quantumleap.core.data.entity.Entity;
import cz.quantumleap.core.person.transport.Person;
import cz.quantumleap.core.tables.PersonTable;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import static cz.quantumleap.core.tables.PersonTable.PERSON;

@Repository
public class PersonDao extends DaoStub<PersonTable> {

    protected PersonDao(DSLContext dslContext, RecordAuditor recordAuditor, EntityRegistry entityRegistry) {
        super(createEntity(), dslContext, recordAuditor, entityRegistry);
    }

    private static Entity<PersonTable> createEntity() {
        return Entity.createBuilder(PERSON)
                .setLookupLabelFieldBuilder(PersonDao::createLookupLabelField)
                .setWordConditionBuilder(PersonDao::createWordCondition)
                .build();
    }

    private static Field<String> createLookupLabelField(Table<?> table) {
        return DSL.coalesce(table.field(PERSON.NAME), table.field(PERSON.EMAIL));
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
