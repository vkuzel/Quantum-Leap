package cz.quantumleap.admin.person;

import cz.quantumleap.core.business.ServiceStub;
import cz.quantumleap.core.person.PersonDao;
import cz.quantumleap.core.person.transport.Person;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import java.util.Objects;

@Service
public class PersonService extends ServiceStub<Person> {

    private final PersonDao personDao;

    public PersonService(PersonDao personDao) {
        super(Person.class, personDao, personDao, personDao);
        this.personDao = personDao;
    }

    @Override
    public Person save(Person person, Errors errors) {
        personDao.fetchByEmail(person.getEmail())
                .filter(p -> !Objects.equals(p.getId(), person.getId()))
                .ifPresent(p -> errors.rejectValue("email", "admin.table.core.person.email.unique"));
        if (errors.hasErrors()) {
            return person;
        } else {
            return super.save(person, errors);
        }
    }
}
