package cz.quantumleap.core.security;

import com.google.common.collect.ImmutableMap;
import cz.quantumleap.core.person.PersonDao;
import cz.quantumleap.core.person.transport.Person;
import cz.quantumleap.core.role.RoleDao;
import cz.quantumleap.core.security.DatabaseAuthoritiesLoader.DatabaseAuthoritiesLoadingException;
import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DatabaseAuthoritiesLoaderTest {

    private static final String USER_EMAIL = "whatever@email.cx";

    @Mock
    private PersonDao personDao;
    @Mock
    private RoleDao roleDao;

    private Person person;

    @Before
    public void mockBeans() {
        person = mock(Person.class);
        doReturn(1L).when(person).getId();
        doReturn(person).when(personDao).fetchByEmail(USER_EMAIL);

        List<String> roles = Collections.singletonList("USER");
        doReturn(roles).when(roleDao).fetchRolesByPersonId(1L);
    }

    @Test
    public void mapAuthorities_success() throws Exception {
        // given
        Map<String, Object> oauthAttributes = ImmutableMap.of("email", USER_EMAIL, "name", "John Doe");
        GrantedAuthority oauthAuthority = new OAuth2UserAuthority(oauthAttributes);
        Collection<GrantedAuthority> oauthAuthorities = Collections.singleton(oauthAuthority);

        // when
        DatabaseAuthoritiesLoader loader = new DatabaseAuthoritiesLoader(personDao, roleDao);
        Collection<? extends GrantedAuthority> authorities = loader.mapAuthorities(oauthAuthorities);

        // then
        verify(personDao, times(1)).fetchByEmail(USER_EMAIL);
        verify(personDao, times(1)).save(person);
        verify(roleDao, times(1)).fetchRolesByPersonId(1L);
        assertThat(authorities, Matchers.contains(hasAuthority("ROLE_USER")));
    }

    @Test(expected = DatabaseAuthoritiesLoadingException.class)
    public void mapAuthorities_unsupportedAuthority() throws Exception {
        // given
        GrantedAuthority oauthAuthority = new SimpleGrantedAuthority("ASD");
        Collection<GrantedAuthority> oauthAuthorities = Collections.singleton(oauthAuthority);

        // when
        DatabaseAuthoritiesLoader loader = new DatabaseAuthoritiesLoader(personDao, roleDao);
        loader.mapAuthorities(oauthAuthorities);
    }

    @Test(expected = DatabaseAuthoritiesLoadingException.class)
    public void mapAuthorities_noAuthority() throws Exception {
        // when
        DatabaseAuthoritiesLoader loader = new DatabaseAuthoritiesLoader(personDao, roleDao);
        loader.mapAuthorities(Collections.emptySet());
    }

    private static Matcher<GrantedAuthority> hasAuthority(String authority) {
        return new CustomTypeSafeMatcher<GrantedAuthority>(authority) {

            @Override
            protected boolean matchesSafely(GrantedAuthority item) {
                return authority.equals(item.getAuthority());
            }
        };
    }
}