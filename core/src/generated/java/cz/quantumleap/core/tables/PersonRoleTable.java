/*
 * This file is generated by jOOQ.
 */
package cz.quantumleap.core.tables;


import cz.quantumleap.core.Core;
import cz.quantumleap.core.Keys;
import cz.quantumleap.core.tables.records.PersonRoleRecord;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;

import java.util.Arrays;
import java.util.List;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class PersonRoleTable extends TableImpl<PersonRoleRecord> {

    private static final long serialVersionUID = 1L;

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
    public final TableField<PersonRoleRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>core.person_role.person_id</code>.
     */
    public final TableField<PersonRoleRecord, Long> PERSON_ID = createField(DSL.name("person_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>core.person_role.role_id</code>.
     */
    public final TableField<PersonRoleRecord, Long> ROLE_ID = createField(DSL.name("role_id"), SQLDataType.BIGINT.nullable(false), this, "");

    private PersonRoleTable(Name alias, Table<PersonRoleRecord> aliased) {
        this(alias, aliased, null);
    }

    private PersonRoleTable(Name alias, Table<PersonRoleRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>core.person_role</code> table reference
     */
    public PersonRoleTable(String alias) {
        this(DSL.name(alias), PERSON_ROLE);
    }

    /**
     * Create an aliased <code>core.person_role</code> table reference
     */
    public PersonRoleTable(Name alias) {
        this(alias, PERSON_ROLE);
    }

    /**
     * Create a <code>core.person_role</code> table reference
     */
    public PersonRoleTable() {
        this(DSL.name("person_role"), null);
    }

    public <O extends Record> PersonRoleTable(Table<O> child, ForeignKey<O, PersonRoleRecord> key) {
        super(child, key, PERSON_ROLE);
    }

    @Override
    public Schema getSchema() {
        return Core.CORE;
    }

    @Override
    public Identity<PersonRoleRecord, Long> getIdentity() {
        return (Identity<PersonRoleRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<PersonRoleRecord> getPrimaryKey() {
        return Keys.PERSON_ROLE_PKEY;
    }

    @Override
    public List<UniqueKey<PersonRoleRecord>> getKeys() {
        return Arrays.<UniqueKey<PersonRoleRecord>>asList(Keys.PERSON_ROLE_PKEY, Keys.PERSON_ROLE_PERSON_ID_ROLE_ID_KEY);
    }

    @Override
    public List<ForeignKey<PersonRoleRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<PersonRoleRecord, ?>>asList(Keys.PERSON_ROLE__PERSON_ROLE_PERSON_ID_FKEY, Keys.PERSON_ROLE__PERSON_ROLE_ROLE_ID_FKEY);
    }

    public PersonTable person() {
        return new PersonTable(this, Keys.PERSON_ROLE__PERSON_ROLE_PERSON_ID_FKEY);
    }

    public RoleTable role() {
        return new RoleTable(this, Keys.PERSON_ROLE__PERSON_ROLE_ROLE_ID_FKEY);
    }

    @Override
    public PersonRoleTable as(String alias) {
        return new PersonRoleTable(DSL.name(alias), this);
    }

    @Override
    public PersonRoleTable as(Name alias) {
        return new PersonRoleTable(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public PersonRoleTable rename(String name) {
        return new PersonRoleTable(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public PersonRoleTable rename(Name name) {
        return new PersonRoleTable(name, null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<Long, Long, Long> fieldsRow() {
        return (Row3) super.fieldsRow();
    }
}