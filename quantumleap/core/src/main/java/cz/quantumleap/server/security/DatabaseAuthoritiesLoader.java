package cz.quantumleap.server.security;

import cz.quantumleap.core.tables.records.PersonRecord;
import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DatabaseAuthoritiesLoader implements AuthoritiesExtractor {

    private static final String OAUTH_DETAILS_EMAIL = "email";
    private static final String ROLE_PREFIX = "ROLE_"; // TODO Use the constrant from the Spring Security!

    private final PersonDao personDao;
    private final RoleDao roleDao;

    DatabaseAuthoritiesLoader(PersonDao personDao, RoleDao roleDao) {
        this.personDao = personDao;
        this.roleDao = roleDao;
    }

    @Override
    public List<GrantedAuthority> extractAuthorities(Map<String, Object> map) {
        String email = getEmail(map);

        PersonRecord personRecord = personDao.findByEmail(email);
        if (personRecord == null) {
            throw new IllegalArgumentException("User " + email + " was not found in database!");
        }

        List<String> roles = roleDao.findRolesByPersonId(personRecord.getId());
        return roles.stream()
                .map(this::convertToAuthority)
                .collect(Collectors.toList());
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

    private String formatDetails(Map<String, Object> map) {
        return map.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(", "));
    }
}
