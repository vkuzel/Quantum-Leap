/*
 * This file is generated by jOOQ.
 */
package cz.quantumleap.core.tables.records;


import cz.quantumleap.core.tables.PersonRoleTable;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class PersonRoleRecord extends UpdatableRecordImpl<PersonRoleRecord> implements Record3<Long, Long, Long> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>core.person_role.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>core.person_role.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>core.person_role.person_id</code>.
     */
    public void setPersonId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>core.person_role.person_id</code>.
     */
    public Long getPersonId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>core.person_role.role_id</code>.
     */
    public void setRoleId(Long value) {
        set(2, value);
    }

    /**
     * Getter for <code>core.person_role.role_id</code>.
     */
    public Long getRoleId() {
        return (Long) get(2);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row3<Long, Long, Long> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    @Override
    public Row3<Long, Long, Long> valuesRow() {
        return (Row3) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return PersonRoleTable.PERSON_ROLE.ID;
    }

    @Override
    public Field<Long> field2() {
        return PersonRoleTable.PERSON_ROLE.PERSON_ID;
    }

    @Override
    public Field<Long> field3() {
        return PersonRoleTable.PERSON_ROLE.ROLE_ID;
    }

    @Override
    public Long component1() {
        return getId();
    }

    @Override
    public Long component2() {
        return getPersonId();
    }

    @Override
    public Long component3() {
        return getRoleId();
    }

    @Override
    public Long value1() {
        return getId();
    }

    @Override
    public Long value2() {
        return getPersonId();
    }

    @Override
    public Long value3() {
        return getRoleId();
    }

    @Override
    public PersonRoleRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public PersonRoleRecord value2(Long value) {
        setPersonId(value);
        return this;
    }

    @Override
    public PersonRoleRecord value3(Long value) {
        setRoleId(value);
        return this;
    }

    @Override
    public PersonRoleRecord values(Long value1, Long value2, Long value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached PersonRoleRecord
     */
    public PersonRoleRecord() {
        super(PersonRoleTable.PERSON_ROLE);
    }

    /**
     * Create a detached, initialised PersonRoleRecord
     */
    public PersonRoleRecord(Long id, Long personId, Long roleId) {
        super(PersonRoleTable.PERSON_ROLE);

        setId(id);
        setPersonId(personId);
        setRoleId(roleId);
        resetChangedOnNotNull();
    }
}
