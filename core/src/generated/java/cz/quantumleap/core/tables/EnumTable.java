/*
 * This file is generated by jOOQ.
 */
package cz.quantumleap.core.tables;


import cz.quantumleap.core.Core;
import cz.quantumleap.core.Keys;
import cz.quantumleap.core.tables.EnumValueTable.EnumValuePath;
import cz.quantumleap.core.tables.records.EnumRecord;

import java.util.Collection;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.ForeignKey;
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
public class EnumTable extends TableImpl<EnumRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>core.enum</code>
     */
    public static final EnumTable ENUM = new EnumTable();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<EnumRecord> getRecordType() {
        return EnumRecord.class;
    }

    /**
     * The column <code>core.enum.id</code>.
     */
    public final TableField<EnumRecord, String> ID = createField(DSL.name("id"), SQLDataType.VARCHAR(255).nullable(false), this, "");

    /**
     * The column <code>core.enum.name</code>.
     */
    public final TableField<EnumRecord, String> NAME = createField(DSL.name("name"), SQLDataType.VARCHAR(255).nullable(false), this, "");

    private EnumTable(Name alias, Table<EnumRecord> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private EnumTable(Name alias, Table<EnumRecord> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
    }

    /**
     * Create an aliased <code>core.enum</code> table reference
     */
    public EnumTable(String alias) {
        this(DSL.name(alias), ENUM);
    }

    /**
     * Create an aliased <code>core.enum</code> table reference
     */
    public EnumTable(Name alias) {
        this(alias, ENUM);
    }

    /**
     * Create a <code>core.enum</code> table reference
     */
    public EnumTable() {
        this(DSL.name("enum"), null);
    }

    public <O extends Record> EnumTable(Table<O> path, ForeignKey<O, EnumRecord> childPath, InverseForeignKey<O, EnumRecord> parentPath) {
        super(path, childPath, parentPath, ENUM);
    }

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    public static class EnumPath extends EnumTable implements Path<EnumRecord> {

        private static final long serialVersionUID = 1L;
        public <O extends Record> EnumPath(Table<O> path, ForeignKey<O, EnumRecord> childPath, InverseForeignKey<O, EnumRecord> parentPath) {
            super(path, childPath, parentPath);
        }
        private EnumPath(Name alias, Table<EnumRecord> aliased) {
            super(alias, aliased);
        }

        @Override
        public EnumPath as(String alias) {
            return new EnumPath(DSL.name(alias), this);
        }

        @Override
        public EnumPath as(Name alias) {
            return new EnumPath(alias, this);
        }

        @Override
        public EnumPath as(Table<?> alias) {
            return new EnumPath(alias.getQualifiedName(), this);
        }
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Core.CORE;
    }

    @Override
    public UniqueKey<EnumRecord> getPrimaryKey() {
        return Keys.ENUM_PKEY;
    }

    private transient EnumValuePath _enumValue;

    /**
     * Get the implicit to-many join path to the <code>core.enum_value</code>
     * table
     */
    public EnumValuePath enumValue() {
        if (_enumValue == null)
            _enumValue = new EnumValuePath(this, null, Keys.ENUM_VALUE__ENUM_VALUE_ENUM_ID_FKEY1.getInverseKey());

        return _enumValue;
    }

    @Override
    public EnumTable as(String alias) {
        return new EnumTable(DSL.name(alias), this);
    }

    @Override
    public EnumTable as(Name alias) {
        return new EnumTable(alias, this);
    }

    @Override
    public EnumTable as(Table<?> alias) {
        return new EnumTable(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public EnumTable rename(String name) {
        return new EnumTable(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public EnumTable rename(Name name) {
        return new EnumTable(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public EnumTable rename(Table<?> name) {
        return new EnumTable(name.getQualifiedName(), null);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public EnumTable where(Condition condition) {
        return new EnumTable(getQualifiedName(), aliased() ? this : null, null, condition);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public EnumTable where(Collection<? extends Condition> conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public EnumTable where(Condition... conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public EnumTable where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public EnumTable where(SQL condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public EnumTable where(@Stringly.SQL String condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public EnumTable where(@Stringly.SQL String condition, Object... binds) {
        return where(DSL.condition(condition, binds));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public EnumTable where(@Stringly.SQL String condition, QueryPart... parts) {
        return where(DSL.condition(condition, parts));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public EnumTable whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public EnumTable whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }
}
