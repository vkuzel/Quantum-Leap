package cz.quantumleap.core.security;

import cz.quantumleap.core.person.PersonDao;
import cz.quantumleap.core.person.transport.Person;
import cz.quantumleap.core.role.RoleDao;
import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class DatabaseAuthoritiesLoader implements AuthoritiesExtractor {

    private static final String OAUTH_DETAILS_EMAIL = "email";
    private static final String OAUTH_DETAILS_NAME = "name";
    private static final String ROLE_PREFIX = "ROLE_"; // TODO Use the constrant from the Spring Security!

    private final PersonDao personDao;
    private final RoleDao roleDao;

    DatabaseAuthoritiesLoader(PersonDao personDao, RoleDao roleDao) {
        this.personDao = personDao;
        this.roleDao = roleDao;
    }

    @Transactional
    @Override
    public List<GrantedAuthority> extractAuthorities(Map<String, Object> map) {
        String email = getEmail(map);

        Optional<Person> personOptional = personDao.fetchByEmail(email);
        if (personOptional.isPresent()) {
            Person person = personOptional.get();

            if (StringUtils.isEmpty(person.getName())) {
                person.setName(getName(map));
                personDao.save(person);
            }

            List<String> roles = roleDao.fetchRolesByPersonId(person.getId());
            return roles.stream()
                    .map(this::convertToAuthority)
                    .collect(Collectors.toList());
        } else {
            throw new IllegalArgumentException("User " + email + " was not found in database!");
        }
    }

    private GrantedAuthority convertToAuthority(String role) {
        String authority = ROLE_PREFIX + role;
        return new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return authority;
            }

            @Override
            public String toString() {
                return authority;
            }
        };
    }

    private String getEmail(Map<String, Object> map) {
        Object email = map.get(OAUTH_DETAILS_EMAIL);
        if (email == null) {
            throw new IllegalArgumentException("Email was not found in OAuth details! " + formatDetails(map));
        }
        if (!(email instanceof String)) {
            throw new IllegalArgumentException("Wrong data type of OAuth email detail! " + email.getClass().getSimpleName());
        }
        return (String) email;
    }

    private String getName(Map<String, Object> map) {
        Object name = map.get(OAUTH_DETAILS_NAME);
        if (name instanceof String) {
            return (String) name;
        }
        return null;
    }

    private String formatDetails(Map<String, Object> map) {
        return map.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(", "));
    }
}
