package cz.quantumleap.core.test.common;

import cz.quantumleap.core.person.PersonDao;
import cz.quantumleap.core.person.domain.Person;
import cz.quantumleap.core.role.RoleDao;
import cz.quantumleap.core.role.domain.Role;
import org.apache.commons.lang3.Validate;
import org.intellij.lang.annotations.Language;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
        var person = new Person();
        person.setEmail("test@email.cx");
        return personDao.save(person);
    }

    @Transactional
    public Role createRole() {
        var role = new Role();
        role.setName("Test role");
        return roleDao.save(role);
    }

    @Transactional
    public void dropSchema(Schema schema) {
        dslContext
                .dropSchemaIfExists(schema)
                .cascade()
                .execute();
    }

    @Transactional
    public void createSchema(Schema schema) {
        dslContext
                .createSchema(schema)
                .execute();
    }

    @Transactional
    public void dropTable(Table<?> table) {
        dslContext
                .dropTableIfExists(table)
                .cascade()
                .execute();
    }

    @Transactional
    public void createTable(Table<?> table) {
        List<Constraint> constraints = new ArrayList<>();
        var primaryKey = table.getPrimaryKey();
        if (primaryKey != null) {
            constraints.add(DSL.primaryKey(primaryKey.getFieldsArray()));
        }
        for (ForeignKey<?, ?> reference : table.getReferences()) {
            constraints.add(DSL.foreignKey(reference.getFieldsArray()).references(reference.getKey().getTable()));
        }
        dslContext
                .createTable(table)
                .columns(table.fields())
                .constraints(constraints)
                .execute();
    }

    @Transactional
    public void insertIntoTable(Table<?> table, Object... values) {
        dslContext
                .insertInto(table)
                .values(values)
                .execute();
    }

    @Transactional
    public void deleteFromTable(String tableName) {
        dslContext.execute("DELETE FROM " + tableName);
    }

    public long countRowsInTable(String tableName) {
        return fetchFirst("SELECT count(*) FROM " + tableName, Long.class);
    }

    public <T> T fetchFirst(@Language("SQL") String sql, Class<T> type) {
        var record = dslContext.fetchOne(sql);
        Validate.notNull(record);
        return record.get(0, type);
    }
}
