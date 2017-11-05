/*
 * This file is generated by jOOQ.
*/
package cz.quantumleap.core.tables;


import cz.quantumleap.core.Core;
import cz.quantumleap.core.Keys;
import cz.quantumleap.core.tables.records.PersonRoleRecord;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.9.6"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class PersonRoleTable extends TableImpl<PersonRoleRecord> {

    private static final long serialVersionUID = -1250412810;

    /**
     * The reference instance of <code>core.person_role</code>
     */
    public static final PersonRoleTable PERSON_ROLE = new PersonRoleTable();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<PersonRoleRecord> getRecordType() {
        return PersonRoleRecord.class;
    }

    /**
     * The column <code>core.person_role.id</code>.
     */
    public final TableField<PersonRoleRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('core.person_role_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>core.person_role.person_id</code>.
     */
    public final TableField<PersonRoleRecord, Long> PERSON_ID = createField("person_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>core.person_role.role_id</code>.
     */
    public final TableField<PersonRoleRecord, Long> ROLE_ID = createField("role_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * Create a <code>core.person_role</code> table reference
     */
    public PersonRoleTable() {
        this("person_role", null);
    }

    /**
     * Create an aliased <code>core.person_role</code> table reference
     */
    public PersonRoleTable(String alias) {
        this(alias, PERSON_ROLE);
    }

    private PersonRoleTable(String alias, Table<PersonRoleRecord> aliased) {
        this(alias, aliased, null);
    }

    private PersonRoleTable(String alias, Table<PersonRoleRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Core.CORE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<PersonRoleRecord, Long> getIdentity() {
        return Keys.IDENTITY_PERSON_ROLE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<PersonRoleRecord> getPrimaryKey() {
        return Keys.PERSON_ROLE_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<PersonRoleRecord>> getKeys() {
        return Arrays.<UniqueKey<PersonRoleRecord>>asList(Keys.PERSON_ROLE_PKEY, Keys.PERSON_ROLE_PERSON_ID_ROLE_ID_KEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<PersonRoleRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<PersonRoleRecord, ?>>asList(Keys.PERSON_ROLE__PERSON_ROLE_PERSON_ID_FKEY, Keys.PERSON_ROLE__PERSON_ROLE_ROLE_ID_FKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PersonRoleTable as(String alias) {
        return new PersonRoleTable(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public PersonRoleTable rename(String name) {
        return new PersonRoleTable(name, null);
    }
}
