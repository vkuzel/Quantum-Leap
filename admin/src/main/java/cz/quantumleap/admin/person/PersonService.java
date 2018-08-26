package cz.quantumleap.admin.person;

import cz.quantumleap.core.business.ServiceStub;
import cz.quantumleap.core.person.PersonDao;
import cz.quantumleap.core.person.transport.Person;
import cz.quantumleap.core.security.DatabaseAuthoritiesLoader;
import org.apache.commons.lang3.Validate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PersonService extends ServiceStub<Person> {

    private final PersonDao personDao;
    private final AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();

    public PersonService(PersonDao personDao) {
        super(Person.class, personDao, personDao, personDao);
        this.personDao = personDao;
    }

    @Cacheable("personByAuthentication")
    public Person fetchByAuthentication(Authentication authentication) {
        Validate.notNull(authentication);
        if (!authentication.isAuthenticated() || trustResolver.isAnonymous(authentication)) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof DefaultOidcUser)) {
            throw new IllegalArgumentException("Unknown principal type " + principal.getClass().getName());
        }

        DefaultOidcUser oidcUser = (DefaultOidcUser) principal;
        Map<String, Object> attributes = oidcUser.getAttributes();
        String email = (String) attributes.get(DatabaseAuthoritiesLoader.OAUTH_DETAILS_EMAIL);
        Validate.notNull(email, "Email was not found in OidcUser details! " + formatDetails(attributes));

        return personDao.fetchByEmail(email);
    }

    private String formatDetails(Map<String, Object> map) {
        return map.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(", "));
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
