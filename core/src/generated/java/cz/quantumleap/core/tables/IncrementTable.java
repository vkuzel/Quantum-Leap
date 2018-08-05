/*
 * This file is generated by jOOQ.
*/
package cz.quantumleap.core.tables;


import cz.quantumleap.core.Core;
import cz.quantumleap.core.Indexes;
import cz.quantumleap.core.Keys;
import cz.quantumleap.core.tables.records.IncrementRecord;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


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
public class IncrementTable extends TableImpl<IncrementRecord> {

    private static final long serialVersionUID = 2121363662;

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
    public final TableField<IncrementRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('core.increment_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>core.increment.module</code>.
     */
    public final TableField<IncrementRecord, String> MODULE = createField("module", org.jooq.impl.SQLDataType.VARCHAR(255).nullable(false), this, "");

    /**
     * The column <code>core.increment.version</code>.
     */
    public final TableField<IncrementRecord, Integer> VERSION = createField("version", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>core.increment.file_name</code>.
     */
    public final TableField<IncrementRecord, String> FILE_NAME = createField("file_name", org.jooq.impl.SQLDataType.VARCHAR(255).nullable(false), this, "");

    /**
     * The column <code>core.increment.created_at</code>.
     */
    public final TableField<IncrementRecord, LocalDateTime> CREATED_AT = createField("created_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * Create a <code>core.increment</code> table reference
     */
    public IncrementTable() {
        this(DSL.name("increment"), null);
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

    private IncrementTable(Name alias, Table<IncrementRecord> aliased) {
        this(alias, aliased, null);
    }

    private IncrementTable(Name alias, Table<IncrementRecord> aliased, Field<?>[] parameters) {
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
        return Arrays.<Index>asList(Indexes.INCREMENT_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<IncrementRecord, Long> getIdentity() {
        return Keys.IDENTITY_INCREMENT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<IncrementRecord> getPrimaryKey() {
        return Keys.INCREMENT_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<IncrementRecord>> getKeys() {
        return Arrays.<UniqueKey<IncrementRecord>>asList(Keys.INCREMENT_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IncrementTable as(String alias) {
        return new IncrementTable(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IncrementTable as(Name alias) {
        return new IncrementTable(alias, this);
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
}
