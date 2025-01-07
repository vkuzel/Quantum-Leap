package cz.quantumleap.core.security;

import cz.quantumleap.core.person.PersonDao;
import cz.quantumleap.core.person.domain.Person;
import cz.quantumleap.core.role.RoleDao;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cz.quantumleap.core.utils.Strings.isBlank;
import static cz.quantumleap.core.utils.Strings.isNotBlank;
import static java.util.Objects.requireNonNull;

/**
 * Loads authorities from database for an user authenticated usually by
 * OAauth 2.0.
 * <p>
 * Because Spring Security 5 (at least in version 5.0.6) does not provide any
 * point that would allow to process an authenticated user's details in a
 * robust manner, the GrantedAuthoritiesMapper.mapAuthorities is hacked to do
 * this.
 * <p>
 * See https://docs.spring.io/spring-security/site/docs/current/reference/html/oauth2login-advanced.html#oauth2login-advanced-map-authorities
 */
@Component
@ConditionalOnWebApplication
public class DatabaseAuthoritiesLoader implements GrantedAuthoritiesMapper {

    public static final String OAUTH_DETAILS_EMAIL = "email";
    private static final String OAUTH_DETAILS_NAME = "name";
    private static final String ROLE_PREFIX = "ROLE_";

    private final PersonDao personDao;
    private final RoleDao roleDao;

    DatabaseAuthoritiesLoader(PersonDao personDao, RoleDao roleDao) {
        this.personDao = personDao;
        this.roleDao = roleDao;
    }

    @Transactional
    @Override
    public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {
        for (var authority : authorities) {
            if (!(authority instanceof OAuth2UserAuthority oAuth2UserAuthority)) {
                continue;
            }

            var person = loadAndUpdatePerson(oAuth2UserAuthority.getAttributes());
            return loadGrantedAuthorities(person);
        }

        var retrievedAuthorities = authorities.stream().map(Object::getClass).map(Class::getName).collect(Collectors.joining(", "));
        throw new DatabaseAuthoritiesLoadingException("No known granted authority in authorities [" + retrievedAuthorities + "]");
    }

    private Person loadAndUpdatePerson(Map<String, Object> authorityAttributes) {
        String email = getAttribute(authorityAttributes, OAUTH_DETAILS_EMAIL);
        requireNonNull(email, "Email was not found in OAuth details! " + formatDetails(authorityAttributes));

        var person = personDao.fetchByEmail(email);
        if (person == null) {
            throw new DatabaseAuthoritiesLoadingException("Email " + email + " was not found in database!");
        }

        String name = getAttribute(authorityAttributes, OAUTH_DETAILS_NAME);
        if (isBlank(person.getName()) && isNotBlank(name)) {
            person.setName(name);
            personDao.save(person);
        }

        return person;
    }

    private List<GrantedAuthority> loadGrantedAuthorities(Person person) {
        return roleDao.fetchRolesByPersonId(person.getId()).stream()
                .map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private <T> T getAttribute(Map<String, Object> attributes, String name) {
        return (T) attributes.get(name);
    }

    private String formatDetails(Map<String, Object> map) {
        return map.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(", "));
    }

    public static class DatabaseAuthoritiesLoadingException extends AuthenticationException {

        private DatabaseAuthoritiesLoadingException(String msg) {
            super(msg);
        }
    }
}
