package cz.quantumleap.admin.person;

import cz.quantumleap.core.business.ServiceStub;
import cz.quantumleap.core.person.PersonDao;
import cz.quantumleap.core.person.transport.Person;
import org.springframework.stereotype.Service;

@Service
public class PersonService extends ServiceStub<Person> {

    public PersonService(PersonDao personDao) {
        super(Person.class, personDao, personDao, personDao);
    }
}
