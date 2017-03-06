package cz.quantumleap.core.person.dao;

import cz.quantumleap.core.persistence.dao.DefaultDao;
import cz.quantumleap.core.persistence.dao.lookup.LookupDaoManager;
import cz.quantumleap.core.person.transport.Person;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static cz.quantumleap.core.tables.PersonTable.PERSON;

@Repository
public class PersonDao extends DefaultDao {

    protected PersonDao(DSLContext dslContext, LookupDaoManager lookupDaoManager) {
        super(PERSON, dslContext, lookupDaoManager);
    }

    public Optional<Person> fetchByEmail(String email) {
        return dslContext.selectFrom(PERSON)
                .where(PERSON.EMAIL.eq(email))
                .fetchOptionalInto(Person.class);
    }
}
