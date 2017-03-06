package cz.quantumleap.core.person;

import cz.quantumleap.core.persistence.Dao;
import cz.quantumleap.core.persistence.RecordAuditor;
import cz.quantumleap.core.persistence.lookup.LookupDaoManager;
import cz.quantumleap.core.person.transport.Person;
import cz.quantumleap.core.tables.PersonTable;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static cz.quantumleap.core.tables.PersonTable.PERSON;

@Repository
public class PersonDao extends Dao<PersonTable> {

    protected PersonDao(DSLContext dslContext, LookupDaoManager lookupDaoManager, RecordAuditor recordAuditor) {
        super(PERSON, dslContext, lookupDaoManager, recordAuditor);
    }

    public Optional<Person> fetchByEmail(String email) {
        return dslContext.selectFrom(PERSON)
                .where(PERSON.EMAIL.eq(email))
                .fetchOptionalInto(Person.class);
    }
}
