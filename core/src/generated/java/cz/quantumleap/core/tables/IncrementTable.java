/*
 * This file is generated by jOOQ.
 */
package cz.quantumleap.core.tables;


import cz.quantumleap.core.Core;
import cz.quantumleap.core.Keys;
import cz.quantumleap.core.tables.records.IncrementRecord;

import java.time.LocalDateTime;
import java.util.function.Function;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function5;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row5;
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
public class IncrementTable extends TableImpl<IncrementRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>core.increment</code>
     */
    public static final IncrementTable INCREMENT = new IncrementTable();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<IncrementRecord> getRecordType() {
        return IncrementRecord.class;
    }

    /**
     * The column <code>core.increment.id</code>.
     */
    public final TableField<IncrementRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>core.increment.module</code>.
     */
    public final TableField<IncrementRecord, String> MODULE = createField(DSL.name("module"), SQLDataType.VARCHAR(255).nullable(false), this, "");

    /**
     * The column <code>core.increment.version</code>.
     */
    public final TableField<IncrementRecord, Integer> VERSION = createField(DSL.name("version"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>core.increment.file_name</code>.
     */
    public final TableField<IncrementRecord, String> FILE_NAME = createField(DSL.name("file_name"), SQLDataType.VARCHAR(255).nullable(false), this, "");

    /**
     * The column <code>core.increment.created_at</code>.
     */
    public final TableField<IncrementRecord, LocalDateTime> CREATED_AT = createField(DSL.name("created_at"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "");

    private IncrementTable(Name alias, Table<IncrementRecord> aliased) {
        this(alias, aliased, null);
    }

    private IncrementTable(Name alias, Table<IncrementRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>core.increment</code> table reference
     */
    public IncrementTable(String alias) {
        this(DSL.name(alias), INCREMENT);
    }

    /**
     * Create an aliased <code>core.increment</code> table reference
     */
    public IncrementTable(Name alias) {
        this(alias, INCREMENT);
    }

    /**
     * Create a <code>core.increment</code> table reference
     */
    public IncrementTable() {
        this(DSL.name("increment"), null);
    }

    public <O extends Record> IncrementTable(Table<O> child, ForeignKey<O, IncrementRecord> key) {
        super(child, key, INCREMENT);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Core.CORE;
    }

    @Override
    public Identity<IncrementRecord, Long> getIdentity() {
        return (Identity<IncrementRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<IncrementRecord> getPrimaryKey() {
        return Keys.INCREMENT_PKEY;
    }

    @Override
    public IncrementTable as(String alias) {
        return new IncrementTable(DSL.name(alias), this);
    }

    @Override
    public IncrementTable as(Name alias) {
        return new IncrementTable(alias, this);
    }

    @Override
    public IncrementTable as(Table<?> alias) {
        return new IncrementTable(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public IncrementTable rename(String name) {
        return new IncrementTable(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public IncrementTable rename(Name name) {
        return new IncrementTable(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public IncrementTable rename(Table<?> name) {
        return new IncrementTable(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row5 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row5<Long, String, Integer, String, LocalDateTime> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function5<? super Long, ? super String, ? super Integer, ? super String, ? super LocalDateTime, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function5<? super Long, ? super String, ? super Integer, ? super String, ? super LocalDateTime, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
