package cz.quantumleap.server.security;

import cz.quantumleap.core.tables.Person;
import cz.quantumleap.core.tables.records.PersonRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static cz.quantumleap.core.tables.Person.PERSON;

@Repository
public class PersonDao {

    private final DSLContext dslContext;

    public PersonDao(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public PersonRecord findByEmail(String email) {
        return dslContext.selectFrom(PERSON)
                .where(PERSON.EMAIL.eq(email))
                .fetchAny();
    }
}
