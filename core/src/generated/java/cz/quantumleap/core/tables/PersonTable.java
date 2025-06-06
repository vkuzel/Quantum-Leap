/*
 * This file is generated by jOOQ.
 */
package cz.quantumleap.core.tables;


import cz.quantumleap.core.Core;
import cz.quantumleap.core.Keys;
import cz.quantumleap.core.tables.NotificationTable.NotificationPath;
import cz.quantumleap.core.tables.PersonRoleTable.PersonRolePath;
import cz.quantumleap.core.tables.RoleTable.RolePath;
import cz.quantumleap.core.tables.SliceQueryTable.SliceQueryPath;
import cz.quantumleap.core.tables.records.PersonRecord;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.InverseForeignKey;
import org.jooq.Name;
import org.jooq.Path;
import org.jooq.PlainSQL;
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.SQL;
import org.jooq.Schema;
import org.jooq.Select;
import org.jooq.Stringly;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class PersonTable extends TableImpl<PersonRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>core.person</code>
     */
    public static final PersonTable PERSON = new PersonTable();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<PersonRecord> getRecordType() {
        return PersonRecord.class;
    }

    /**
     * The column <code>core.person.id</code>.
     */
    public final TableField<PersonRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>core.person.email</code>.
     */
    public final TableField<PersonRecord, String> EMAIL = createField(DSL.name("email"), SQLDataType.VARCHAR(255).nullable(false), this, "");

    /**
     * The column <code>core.person.name</code>.
     */
    public final TableField<PersonRecord, String> NAME = createField(DSL.name("name"), SQLDataType.VARCHAR(255), this, "");

    /**
     * The column <code>core.person.created_at</code>.
     */
    public final TableField<PersonRecord, LocalDateTime> CREATED_AT = createField(DSL.name("created_at"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "");

    private PersonTable(Name alias, Table<PersonRecord> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private PersonTable(Name alias, Table<PersonRecord> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
    }

    /**
     * Create an aliased <code>core.person</code> table reference
     */
    public PersonTable(String alias) {
        this(DSL.name(alias), PERSON);
    }

    /**
     * Create an aliased <code>core.person</code> table reference
     */
    public PersonTable(Name alias) {
        this(alias, PERSON);
    }

    /**
     * Create a <code>core.person</code> table reference
     */
    public PersonTable() {
        this(DSL.name("person"), null);
    }

    public <O extends Record> PersonTable(Table<O> path, ForeignKey<O, PersonRecord> childPath, InverseForeignKey<O, PersonRecord> parentPath) {
        super(path, childPath, parentPath, PERSON);
    }

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    public static class PersonPath extends PersonTable implements Path<PersonRecord> {

        private static final long serialVersionUID = 1L;
        public <O extends Record> PersonPath(Table<O> path, ForeignKey<O, PersonRecord> childPath, InverseForeignKey<O, PersonRecord> parentPath) {
            super(path, childPath, parentPath);
        }
        private PersonPath(Name alias, Table<PersonRecord> aliased) {
            super(alias, aliased);
        }

        @Override
        public PersonPath as(String alias) {
            return new PersonPath(DSL.name(alias), this);
        }

        @Override
        public PersonPath as(Name alias) {
            return new PersonPath(alias, this);
        }

        @Override
        public PersonPath as(Table<?> alias) {
            return new PersonPath(alias.getQualifiedName(), this);
        }
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Core.CORE;
    }

    @Override
    public Identity<PersonRecord, Long> getIdentity() {
        return (Identity<PersonRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<PersonRecord> getPrimaryKey() {
        return Keys.PERSON_PKEY;
    }

    @Override
    public List<UniqueKey<PersonRecord>> getUniqueKeys() {
        return Arrays.asList(Keys.PERSON_EMAIL_KEY);
    }

    private transient NotificationPath _notificationPersonIdFkey;

    /**
     * Get the implicit to-many join path to the <code>core.notification</code>
     * table, via the <code>notification_person_id_fkey</code> key
     */
    public NotificationPath notificationPersonIdFkey() {
        if (_notificationPersonIdFkey == null)
            _notificationPersonIdFkey = new NotificationPath(this, null, Keys.NOTIFICATION__NOTIFICATION_PERSON_ID_FKEY.getInverseKey());

        return _notificationPersonIdFkey;
    }

    private transient NotificationPath _notificationResolvedByFkey;

    /**
     * Get the implicit to-many join path to the <code>core.notification</code>
     * table, via the <code>notification_resolved_by_fkey</code> key
     */
    public NotificationPath notificationResolvedByFkey() {
        if (_notificationResolvedByFkey == null)
            _notificationResolvedByFkey = new NotificationPath(this, null, Keys.NOTIFICATION__NOTIFICATION_RESOLVED_BY_FKEY.getInverseKey());

        return _notificationResolvedByFkey;
    }

    private transient PersonRolePath _personRole;

    /**
     * Get the implicit to-many join path to the <code>core.person_role</code>
     * table
     */
    public PersonRolePath personRole() {
        if (_personRole == null)
            _personRole = new PersonRolePath(this, null, Keys.PERSON_ROLE__PERSON_ROLE_PERSON_ID_FKEY.getInverseKey());

        return _personRole;
    }

    private transient SliceQueryPath _sliceQuery;

    /**
     * Get the implicit to-many join path to the <code>core.slice_query</code>
     * table
     */
    public SliceQueryPath sliceQuery() {
        if (_sliceQuery == null)
            _sliceQuery = new SliceQueryPath(this, null, Keys.SLICE_QUERY__SLICE_QUERY_PERSON_ID_FKEY.getInverseKey());

        return _sliceQuery;
    }

    /**
     * Get the implicit many-to-many join path to the <code>core.role</code>
     * table
     */
    public RolePath role() {
        return personRole().role();
    }

    @Override
    public PersonTable as(String alias) {
        return new PersonTable(DSL.name(alias), this);
    }

    @Override
    public PersonTable as(Name alias) {
        return new PersonTable(alias, this);
    }

    @Override
    public PersonTable as(Table<?> alias) {
        return new PersonTable(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public PersonTable rename(String name) {
        return new PersonTable(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public PersonTable rename(Name name) {
        return new PersonTable(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public PersonTable rename(Table<?> name) {
        return new PersonTable(name.getQualifiedName(), null);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public PersonTable where(Condition condition) {
        return new PersonTable(getQualifiedName(), aliased() ? this : null, null, condition);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public PersonTable where(Collection<? extends Condition> conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public PersonTable where(Condition... conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public PersonTable where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public PersonTable where(SQL condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public PersonTable where(@Stringly.SQL String condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public PersonTable where(@Stringly.SQL String condition, Object... binds) {
        return where(DSL.condition(condition, binds));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public PersonTable where(@Stringly.SQL String condition, QueryPart... parts) {
        return where(DSL.condition(condition, parts));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public PersonTable whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public PersonTable whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }
}
