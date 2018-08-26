/*
 * This file is generated by jOOQ.
*/
package cz.quantumleap.core.tables;


import cz.quantumleap.core.Core;
import cz.quantumleap.core.Indexes;
import cz.quantumleap.core.Keys;
import cz.quantumleap.core.tables.records.EnumValueRecord;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;

import javax.annotation.Generated;
import java.util.Arrays;
import java.util.List;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.7"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class EnumValueTable extends TableImpl<EnumValueRecord> {

    private static final long serialVersionUID = -1667356037;

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
    public final TableField<EnumValueRecord, String> ID = createField("id", org.jooq.impl.SQLDataType.VARCHAR(255).nullable(false), this, "");

    /**
     * The column <code>core.enum_value.enum_id</code>.
     */
    public final TableField<EnumValueRecord, String> ENUM_ID = createField("enum_id", org.jooq.impl.SQLDataType.VARCHAR(255).nullable(false), this, "");

    /**
     * The column <code>core.enum_value.label</code>.
     */
    public final TableField<EnumValueRecord, String> LABEL = createField("label", org.jooq.impl.SQLDataType.VARCHAR(255).nullable(false), this, "");

    /**
     * Create a <code>core.enum_value</code> table reference
     */
    public EnumValueTable() {
        this(DSL.name("enum_value"), null);
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

    private EnumValueTable(Name alias, Table<EnumValueRecord> aliased) {
        this(alias, aliased, null);
    }

    private EnumValueTable(Name alias, Table<EnumValueRecord> aliased, Field<?>[] parameters) {
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
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.ENUM_VALUE_ID_ENUM_ID_KEY, Indexes.ENUM_VALUE_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<EnumValueRecord> getPrimaryKey() {
        return Keys.ENUM_VALUE_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<EnumValueRecord>> getKeys() {
        return Arrays.<UniqueKey<EnumValueRecord>>asList(Keys.ENUM_VALUE_PKEY, Keys.ENUM_VALUE_ID_ENUM_ID_KEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<EnumValueRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<EnumValueRecord, ?>>asList(Keys.ENUM_VALUE__ENUM_VALUE_ENUM_ID_FKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EnumValueTable as(String alias) {
        return new EnumValueTable(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
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
}
