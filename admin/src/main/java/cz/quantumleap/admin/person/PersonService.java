package cz.quantumleap.admin.person;

import cz.quantumleap.core.business.ServiceStub;
import cz.quantumleap.core.person.PersonDao;
import cz.quantumleap.core.person.domain.Person;
import cz.quantumleap.core.security.AuthenticationEmailResolver;
import org.apache.commons.lang3.Validate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import java.util.Objects;

@Service
public class PersonService extends ServiceStub<Person> {

    private final PersonDao personDao;
    private final AuthenticationEmailResolver authenticationEmailResolver = new AuthenticationEmailResolver();

    public PersonService(PersonDao personDao) {
        super(Person.class, personDao, personDao, personDao);
        this.personDao = personDao;
    }

    @Cacheable("personByAuthentication")
    public Person fetchByAuthentication(Authentication authentication) {
        Validate.notNull(authentication);
        String email = authenticationEmailResolver.resolve(authentication);
        return personDao.fetchByEmail(email);
    }

    @Override
    public Person save(Person person, Errors errors) {
        Person existingPerson = personDao.fetchByEmail(person.getEmail());
        if (existingPerson != null && !Objects.equals(existingPerson.getId(), person.getId())) {
            errors.rejectValue("email", "admin.table.core.person.email.unique");
        }

        if (errors.hasErrors()) {
            return person;
        } else {
            return super.save(person, errors);
        }
    }
}
