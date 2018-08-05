package cz.quantumleap.core.person;

import cz.quantumleap.core.data.DaoStub;
import cz.quantumleap.core.data.EnumManager;
import cz.quantumleap.core.data.LookupDaoManager;
import cz.quantumleap.core.data.RecordAuditor;
import cz.quantumleap.core.person.transport.Person;
import cz.quantumleap.core.tables.PersonTable;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static cz.quantumleap.core.tables.PersonTable.PERSON;

@Repository
public class PersonDao extends DaoStub<PersonTable> {

    protected PersonDao(DSLContext dslContext, LookupDaoManager lookupDaoManager, EnumManager enumManager, RecordAuditor recordAuditor) {
        super(PERSON, PERSON.NAME.coalesce(PERSON.EMAIL), s -> PERSON.NAME.startsWith(s).or(PERSON.EMAIL.startsWith(s)), dslContext, lookupDaoManager, enumManager, recordAuditor);
    }

    public Person fetchByEmail(String email) {
        return dslContext.selectFrom(PERSON)
                .where(PERSON.EMAIL.eq(email))
                .fetchAnyInto(Person.class);
    }
}
