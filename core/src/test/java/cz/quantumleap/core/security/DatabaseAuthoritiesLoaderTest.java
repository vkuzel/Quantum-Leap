package cz.quantumleap.core.security;

import com.google.common.collect.ImmutableMap;
import cz.quantumleap.core.person.PersonDao;
import cz.quantumleap.core.person.transport.Person;
import cz.quantumleap.core.role.RoleDao;
import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DatabaseAuthoritiesLoaderTest {

    @Mock
    private PersonDao personDao;
    @Mock
    private RoleDao roleDao;

    @Test
    public void extractAuthorities() throws Exception {
        // given
        String email = "whatever@email.com";
        Map<String, Object> details = ImmutableMap.of("email", email);

        Person person = mock(Person.class);
        doReturn(1L).when(person).getId();
        Optional<Person> personOptional = Optional.of(person);
        doReturn(personOptional).when(personDao).fetchByEmail(email);

        List<String> roles = Collections.singletonList("USER");
        doReturn(roles).when(roleDao).fetchRolesByPersonId(1L);

        // when
        DatabaseAuthoritiesLoader loader = new DatabaseAuthoritiesLoader(personDao, roleDao);
        List<GrantedAuthority> authorities = loader.extractAuthorities(details);

        // then
        verify(personDao, times(1)).fetchByEmail(email);
        verify(personDao, times(1)).save(person);
        verify(roleDao, times(1)).fetchRolesByPersonId(1L);
        assertThat(authorities, Matchers.contains(hasAuthority("ROLE_USER")));
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