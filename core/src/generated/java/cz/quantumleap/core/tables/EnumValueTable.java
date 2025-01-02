/*
 * This file is generated by jOOQ.
 */
package cz.quantumleap.core.tables;


import cz.quantumleap.core.Core;
import cz.quantumleap.core.Keys;
import cz.quantumleap.core.tables.EnumTable.EnumPath;
import cz.quantumleap.core.tables.records.EnumValueRecord;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private EnumValueTable(Name alias, Table<EnumValueRecord> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
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

    public <O extends Record> EnumValueTable(Table<O> path, ForeignKey<O, EnumValueRecord> childPath, InverseForeignKey<O, EnumValueRecord> parentPath) {
        super(path, childPath, parentPath, ENUM_VALUE);
    }

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    public static class EnumValuePath extends EnumValueTable implements Path<EnumValueRecord> {

        private static final long serialVersionUID = 1L;
        public <O extends Record> EnumValuePath(Table<O> path, ForeignKey<O, EnumValueRecord> childPath, InverseForeignKey<O, EnumValueRecord> parentPath) {
            super(path, childPath, parentPath);
        }
        private EnumValuePath(Name alias, Table<EnumValueRecord> aliased) {
            super(alias, aliased);
        }

        @Override
        public EnumValuePath as(String alias) {
            return new EnumValuePath(DSL.name(alias), this);
        }

        @Override
        public EnumValuePath as(Name alias) {
            return new EnumValuePath(alias, this);
        }

        @Override
        public EnumValuePath as(Table<?> alias) {
            return new EnumValuePath(alias.getQualifiedName(), this);
        }
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Core.CORE;
    }

    @Override
    public UniqueKey<EnumValueRecord> getPrimaryKey() {
        return Keys.ENUM_VALUE_PKEY1;
    }

    @Override
    public List<ForeignKey<EnumValueRecord, ?>> getReferences() {
        return Arrays.asList(Keys.ENUM_VALUE__ENUM_VALUE_ENUM_ID_FKEY1);
    }

    private transient EnumPath _enum_;

    /**
     * Get the implicit join path to the <code>core.enum</code> table.
     */
    public EnumPath enum_() {
        if (_enum_ == null)
            _enum_ = new EnumPath(this, Keys.ENUM_VALUE__ENUM_VALUE_ENUM_ID_FKEY1, null);

        return _enum_;
    }

    @Override
    public EnumValueTable as(String alias) {
        return new EnumValueTable(DSL.name(alias), this);
    }

    @Override
    public EnumValueTable as(Name alias) {
        return new EnumValueTable(alias, this);
    }

    @Override
    public EnumValueTable as(Table<?> alias) {
        return new EnumValueTable(alias.getQualifiedName(), this);
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

    /**
     * Rename this table
     */
    @Override
    public EnumValueTable rename(Table<?> name) {
        return new EnumValueTable(name.getQualifiedName(), null);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public EnumValueTable where(Condition condition) {
        return new EnumValueTable(getQualifiedName(), aliased() ? this : null, null, condition);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public EnumValueTable where(Collection<? extends Condition> conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public EnumValueTable where(Condition... conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public EnumValueTable where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public EnumValueTable where(SQL condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public EnumValueTable where(@Stringly.SQL String condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public EnumValueTable where(@Stringly.SQL String condition, Object... binds) {
        return where(DSL.condition(condition, binds));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public EnumValueTable where(@Stringly.SQL String condition, QueryPart... parts) {
        return where(DSL.condition(condition, parts));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public EnumValueTable whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public EnumValueTable whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }
}
