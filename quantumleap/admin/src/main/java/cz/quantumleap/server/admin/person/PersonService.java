package cz.quantumleap.server.admin.person;

import cz.quantumleap.core.common.NotFoundException;
import cz.quantumleap.core.persistence.transport.Slice;
import cz.quantumleap.core.persistence.transport.SliceRequest;
import cz.quantumleap.core.person.PersonDao;
import cz.quantumleap.core.person.transport.Person;
import org.springframework.stereotype.Service;

@Service
public class PersonService {

    private final PersonDao personDao;

    public PersonService(PersonDao personDao) {
        this.personDao = personDao;
    }

    public Slice findPeople(SliceRequest sliceRequest) {
        return personDao.fetchSlice(sliceRequest);
    }

    public Person getPerson(long id) {
        return personDao.fetchById(id, Person.class).orElseThrow(() -> new NotFoundException(id));
    }

    public Person savePerson(Person person) {
        return personDao.save(person);
    }
}
