package cz.quantumleap.server.security;

import com.google.common.collect.ImmutableMap;
import cz.quantumleap.core.tables.records.PersonRecord;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
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

        PersonRecord person = mock(PersonRecord.class);
        doReturn(1L).when(person).getId();
        doReturn(person).when(personDao).findByEmail(email);

        List<String> roles = Collections.singletonList("USER");
        doReturn(roles).when(roleDao).findRolesByPersonId(1L);

        // when
        DatabaseAuthoritiesLoader loader = new DatabaseAuthoritiesLoader(personDao, roleDao);
        List<GrantedAuthority> authorities = loader.extractAuthorities(details);

        // then
        verify(personDao, times(1)).findByEmail(email);
        verify(roleDao, times(1)).findRolesByPersonId(1L);
        assertEquals("ROLE_USER", authorities.get(0).getAuthority());
    }
}