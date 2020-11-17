/*
 * This file is generated by jOOQ.
 */
package cz.quantumleap.core.tables;


import cz.quantumleap.core.Core;
import cz.quantumleap.core.Keys;
import cz.quantumleap.core.tables.records.EnumValueRecord;
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
public class EnumValueTable extends TableImpl<EnumValueRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>core.enum_value</code>
     */
    public static final EnumValueTable ENUM_VALUE = new EnumValueTable();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<EnumValueRecord> getRecordType() {
        return EnumValueRecord.class;
    }

    /**
     * The column <code>core.enum_value.id</code>.
     */
    public final TableField<EnumValueRecord, String> ID = createField(DSL.name("id"), SQLDataType.VARCHAR(255).nullable(false), this, "");

    /**
     * The column <code>core.enum_value.enum_id</code>.
     */
    public final TableField<EnumValueRecord, String> ENUM_ID = createField(DSL.name("enum_id"), SQLDataType.VARCHAR(255).nullable(false), this, "");

    /**
     * The column <code>core.enum_value.label</code>.
     */
    public final TableField<EnumValueRecord, String> LABEL = createField(DSL.name("label"), SQLDataType.VARCHAR(255).nullable(false), this, "");

    private EnumValueTable(Name alias, Table<EnumValueRecord> aliased) {
        this(alias, aliased, null);
    }

    private EnumValueTable(Name alias, Table<EnumValueRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>core.enum_value</code> table reference
     */
    public EnumValueTable(String alias) {
        this(DSL.name(alias), ENUM_VALUE);
    }

    /**
     * Create an aliased <code>core.enum_value</code> table reference
     */
    public EnumValueTable(Name alias) {
        this(alias, ENUM_VALUE);
    }

    /**
     * Create a <code>core.enum_value</code> table reference
     */
    public EnumValueTable() {
        this(DSL.name("enum_value"), null);
    }

    public <O extends Record> EnumValueTable(Table<O> child, ForeignKey<O, EnumValueRecord> key) {
        super(child, key, ENUM_VALUE);
    }

    @Override
    public Schema getSchema() {
        return Core.CORE;
    }

    @Override
    public UniqueKey<EnumValueRecord> getPrimaryKey() {
        return Keys.ENUM_VALUE_PKEY1;
    }

    @Override
    public List<UniqueKey<EnumValueRecord>> getKeys() {
        return Arrays.<UniqueKey<EnumValueRecord>>asList(Keys.ENUM_VALUE_PKEY1);
    }

    @Override
    public List<ForeignKey<EnumValueRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<EnumValueRecord, ?>>asList(Keys.ENUM_VALUE__ENUM_VALUE_ENUM_ID_FKEY1);
    }

    public EnumTable enum_() {
        return new EnumTable(this, Keys.ENUM_VALUE__ENUM_VALUE_ENUM_ID_FKEY1);
    }

    @Override
    public EnumValueTable as(String alias) {
        return new EnumValueTable(DSL.name(alias), this);
    }

    @Override
    public EnumValueTable as(Name alias) {
        return new EnumValueTable(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public EnumValueTable rename(String name) {
        return new EnumValueTable(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public EnumValueTable rename(Name name) {
        return new EnumValueTable(name, null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<String, String, String> fieldsRow() {
        return (Row3) super.fieldsRow();
    }
}