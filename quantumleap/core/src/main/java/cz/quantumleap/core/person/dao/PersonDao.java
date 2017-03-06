package cz.quantumleap.core.person.dao;

import cz.quantumleap.core.persistence.RecordAuditor;
import cz.quantumleap.core.persistence.dao.Dao;
import cz.quantumleap.core.persistence.dao.lookup.LookupDaoManager;
import cz.quantumleap.core.person.transport.Person;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static cz.quantumleap.core.tables.PersonTable.PERSON;

@Repository
public class PersonDao extends Dao {

    protected PersonDao(DSLContext dslContext, LookupDaoManager lookupDaoManager, RecordAuditor recordAuditor) {
        super(PERSON, dslContext, lookupDaoManager, recordAuditor);
    }

    public Optional<Person> fetchByEmail(String email) {
        return dslContext.selectFrom(PERSON)
                .where(PERSON.EMAIL.eq(email))
                .fetchOptionalInto(Person.class);
    }
}
