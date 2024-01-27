/*
 * This file is generated by jOOQ.
 */
package cz.quantumleap.core.tables;


import cz.quantumleap.core.Core;
import cz.quantumleap.core.Keys;
import cz.quantumleap.core.tables.records.NotificationRecord;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function8;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row8;
import org.jooq.Schema;
import org.jooq.SelectField;
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
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class NotificationTable extends TableImpl<NotificationRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>core.notification</code>
     */
    public static final NotificationTable NOTIFICATION = new NotificationTable();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<NotificationRecord> getRecordType() {
        return NotificationRecord.class;
    }

    /**
     * The column <code>core.notification.id</code>.
     */
    public final TableField<NotificationRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>core.notification.code</code>.
     */
    public final TableField<NotificationRecord, String> CODE = createField(DSL.name("code"), SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>core.notification.message_arguments</code>.
     */
    public final TableField<NotificationRecord, String[]> MESSAGE_ARGUMENTS = createField(DSL.name("message_arguments"), SQLDataType.VARCHAR.nullable(false).array(), this, "");

    /**
     * The column <code>core.notification.person_id</code>.
     */
    public final TableField<NotificationRecord, Long> PERSON_ID = createField(DSL.name("person_id"), SQLDataType.BIGINT, this, "");

    /**
     * The column <code>core.notification.role_id</code>.
     */
    public final TableField<NotificationRecord, Long> ROLE_ID = createField(DSL.name("role_id"), SQLDataType.BIGINT, this, "");

    /**
     * The column <code>core.notification.created_at</code>.
     */
    public final TableField<NotificationRecord, LocalDateTime> CREATED_AT = createField(DSL.name("created_at"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "");

    /**
     * The column <code>core.notification.resolved_at</code>.
     */
    public final TableField<NotificationRecord, LocalDateTime> RESOLVED_AT = createField(DSL.name("resolved_at"), SQLDataType.LOCALDATETIME(6), this, "");

    /**
     * The column <code>core.notification.resolved_by</code>.
     */
    public final TableField<NotificationRecord, Long> RESOLVED_BY = createField(DSL.name("resolved_by"), SQLDataType.BIGINT, this, "");

    private NotificationTable(Name alias, Table<NotificationRecord> aliased) {
        this(alias, aliased, null);
    }

    private NotificationTable(Name alias, Table<NotificationRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>core.notification</code> table reference
     */
    public NotificationTable(String alias) {
        this(DSL.name(alias), NOTIFICATION);
    }

    /**
     * Create an aliased <code>core.notification</code> table reference
     */
    public NotificationTable(Name alias) {
        this(alias, NOTIFICATION);
    }

    /**
     * Create a <code>core.notification</code> table reference
     */
    public NotificationTable() {
        this(DSL.name("notification"), null);
    }

    public <O extends Record> NotificationTable(Table<O> child, ForeignKey<O, NotificationRecord> key) {
        super(child, key, NOTIFICATION);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Core.CORE;
    }

    @Override
    public Identity<NotificationRecord, Long> getIdentity() {
        return (Identity<NotificationRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<NotificationRecord> getPrimaryKey() {
        return Keys.NOTIFICATION_PKEY;
    }

    @Override
    public List<ForeignKey<NotificationRecord, ?>> getReferences() {
        return Arrays.asList(Keys.NOTIFICATION__NOTIFICATION_PERSON_ID_FKEY, Keys.NOTIFICATION__NOTIFICATION_ROLE_ID_FKEY, Keys.NOTIFICATION__NOTIFICATION_RESOLVED_BY_FKEY);
    }

    private transient PersonTable _notificationPersonIdFkey;
    private transient RoleTable _role;
    private transient PersonTable _notificationResolvedByFkey;

    /**
     * Get the implicit join path to the <code>core.person</code> table, via the
     * <code>notification_person_id_fkey</code> key.
     */
    public PersonTable notificationPersonIdFkey() {
        if (_notificationPersonIdFkey == null)
            _notificationPersonIdFkey = new PersonTable(this, Keys.NOTIFICATION__NOTIFICATION_PERSON_ID_FKEY);

        return _notificationPersonIdFkey;
    }

    /**
     * Get the implicit join path to the <code>core.role</code> table.
     */
    public RoleTable role() {
        if (_role == null)
            _role = new RoleTable(this, Keys.NOTIFICATION__NOTIFICATION_ROLE_ID_FKEY);

        return _role;
    }

    /**
     * Get the implicit join path to the <code>core.person</code> table, via the
     * <code>notification_resolved_by_fkey</code> key.
     */
    public PersonTable notificationResolvedByFkey() {
        if (_notificationResolvedByFkey == null)
            _notificationResolvedByFkey = new PersonTable(this, Keys.NOTIFICATION__NOTIFICATION_RESOLVED_BY_FKEY);

        return _notificationResolvedByFkey;
    }

    @Override
    public NotificationTable as(String alias) {
        return new NotificationTable(DSL.name(alias), this);
    }

    @Override
    public NotificationTable as(Name alias) {
        return new NotificationTable(alias, this);
    }

    @Override
    public NotificationTable as(Table<?> alias) {
        return new NotificationTable(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public NotificationTable rename(String name) {
        return new NotificationTable(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public NotificationTable rename(Name name) {
        return new NotificationTable(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public NotificationTable rename(Table<?> name) {
        return new NotificationTable(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row8 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row8<Long, String, String[], Long, Long, LocalDateTime, LocalDateTime, Long> fieldsRow() {
        return (Row8) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function8<? super Long, ? super String, ? super String[], ? super Long, ? super Long, ? super LocalDateTime, ? super LocalDateTime, ? super Long, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function8<? super Long, ? super String, ? super String[], ? super Long, ? super Long, ? super LocalDateTime, ? super LocalDateTime, ? super Long, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
