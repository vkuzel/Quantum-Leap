package cz.quantumleap.core.test.common;

import cz.quantumleap.core.person.PersonDao;
import cz.quantumleap.core.person.transport.Person;
import cz.quantumleap.core.role.RoleDao;
import cz.quantumleap.core.role.transport.Role;
import org.apache.commons.lang3.Validate;
import org.intellij.lang.annotations.Language;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CoreTestSupport {

    private final DSLContext dslContext;
    private final PersonDao personDao;
    private final RoleDao roleDao;

    public CoreTestSupport(DSLContext dslContext, PersonDao personDao, RoleDao roleDao) {
        this.dslContext = dslContext;
        this.personDao = personDao;
        this.roleDao = roleDao;
    }

    @Transactional
    public void clearDatabase() {
        deleteFromTable("core.person");
        deleteFromTable("core.role");
    }

    @Transactional
    public Person createPerson() {
        Person person = new Person();
        person.setEmail("test@email.cx");
        return personDao.save(person);
    }

    @Transactional
    public Role createRole() {
        Role role = new Role();
        role.setName("Test role");
        return roleDao.save(role);
    }

    @Transactional
    public void deleteFromTable(String tableName) {
        dslContext.execute("DELETE FROM " + tableName);
    }

    public long countRowsInTable(String tableName) {
        return fetchFirst("SELECT count(*) FROM " + tableName, Long.class);
    }

    public <T> T fetchFirst(@Language("SQL") String sql, Class<T> type) {
        Record record = dslContext.fetchOne(sql);
        Validate.notNull(record);
        return record.get(0, type);
    }
}
