package cz.quantumleap.core.security;

import com.google.common.collect.ImmutableMap;
import cz.quantumleap.core.person.PersonDao;
import cz.quantumleap.core.person.domain.Person;
import cz.quantumleap.core.role.RoleDao;
import cz.quantumleap.core.security.DatabaseAuthoritiesLoader.DatabaseAuthoritiesLoadingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

public class DatabaseAuthoritiesLoaderTest {

    private static final String USER_EMAIL = "whatever@email.cx";

    private PersonDao personDao;
    private RoleDao roleDao;
    private Person person;

    @BeforeEach
    public void mockBeans() {
        personDao = mock(PersonDao.class);

        roleDao = mock(RoleDao.class);
        List<String> roles = Collections.singletonList("USER");
        doReturn(roles).when(roleDao).fetchRolesByPersonId(1L);

        person = mock(Person.class);
        doReturn(1L).when(person).getId();
        doReturn(person).when(personDao).fetchByEmail(USER_EMAIL);
    }

    @Test
    public void grantedAuthorityIsMappedForUser() {
        // given
        Map<String, Object> oauthAttributes = ImmutableMap.of("email", USER_EMAIL, "name", "John Doe");
        GrantedAuthority oauthAuthority = new OAuth2UserAuthority(oauthAttributes);
        Collection<GrantedAuthority> oauthAuthorities = Collections.singleton(oauthAuthority);
        DatabaseAuthoritiesLoader loader = new DatabaseAuthoritiesLoader(personDao, roleDao);

        // when
        Collection<? extends GrantedAuthority> authorities = loader.mapAuthorities(oauthAuthorities);

        // then
        verify(personDao, times(1)).fetchByEmail(USER_EMAIL);
        verify(personDao, times(1)).save(person);
        verify(roleDao, times(1)).fetchRolesByPersonId(1L);
        Assertions.assertTrue(authorities.stream().anyMatch(i -> i.getAuthority().equals("ROLE_USER")));
    }

    @Test
    public void unsupportedAuthorityMappingFails() {
        // given
        GrantedAuthority oauthAuthority = new SimpleGrantedAuthority("ASD");
        Collection<GrantedAuthority> oauthAuthorities = Collections.singleton(oauthAuthority);
        DatabaseAuthoritiesLoader loader = new DatabaseAuthoritiesLoader(personDao, roleDao);

        // when...then
        Assertions.assertThrows(DatabaseAuthoritiesLoadingException.class, () ->
                loader.mapAuthorities(oauthAuthorities));
    }

    @Test
    public void missingAuthorityMappingFails() {
        // Given
        DatabaseAuthoritiesLoader loader = new DatabaseAuthoritiesLoader(personDao, roleDao);

        // when...then
        Assertions.assertThrows(DatabaseAuthoritiesLoadingException.class, () ->
                loader.mapAuthorities(Collections.emptySet()));
    }
}
