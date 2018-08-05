/*
 * This file is generated by jOOQ.
*/
package cz.quantumleap.core.tables;


import cz.quantumleap.core.Core;
import cz.quantumleap.core.Indexes;
import cz.quantumleap.core.Keys;
import cz.quantumleap.core.tables.records.TablePreferencesRecord;

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
public class TablePreferencesTable extends TableImpl<TablePreferencesRecord> {

    private static final long serialVersionUID = -790655372;

    /**
     * The reference instance of <code>core.table_preferences</code>
     */
    public static final TablePreferencesTable TABLE_PREFERENCES = new TablePreferencesTable();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TablePreferencesRecord> getRecordType() {
        return TablePreferencesRecord.class;
    }

    /**
     * The column <code>core.table_preferences.id</code>.
     */
    public final TableField<TablePreferencesRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('core.table_preferences_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>core.table_preferences.database_table_name_with_schema</code>.
     */
    public final TableField<TablePreferencesRecord, String> DATABASE_TABLE_NAME_WITH_SCHEMA = createField("database_table_name_with_schema", org.jooq.impl.SQLDataType.VARCHAR(128).nullable(false), this, "");

    /**
     * The column <code>core.table_preferences.is_default</code>.
     */
    public final TableField<TablePreferencesRecord, Boolean> IS_DEFAULT = createField("is_default", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false), this, "");

    /**
     * The column <code>core.table_preferences.enabled_columns</code>.
     */
    public final TableField<TablePreferencesRecord, String[]> ENABLED_COLUMNS = createField("enabled_columns", org.jooq.impl.SQLDataType.VARCHAR.getArrayDataType(), this, "");

    /**
     * Create a <code>core.table_preferences</code> table reference
     */
    public TablePreferencesTable() {
        this(DSL.name("table_preferences"), null);
    }

    /**
     * Create an aliased <code>core.table_preferences</code> table reference
     */
    public TablePreferencesTable(String alias) {
        this(DSL.name(alias), TABLE_PREFERENCES);
    }

    /**
     * Create an aliased <code>core.table_preferences</code> table reference
     */
    public TablePreferencesTable(Name alias) {
        this(alias, TABLE_PREFERENCES);
    }

    private TablePreferencesTable(Name alias, Table<TablePreferencesRecord> aliased) {
        this(alias, aliased, null);
    }

    private TablePreferencesTable(Name alias, Table<TablePreferencesRecord> aliased, Field<?>[] parameters) {
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
        return Arrays.<Index>asList(Indexes.TABLE_PREFERENCES_DATABASE_TABLE_NAME_WITH_SCHEMA_IS_DEFAUL_KEY, Indexes.TABLE_PREFERENCES_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<TablePreferencesRecord, Long> getIdentity() {
        return Keys.IDENTITY_TABLE_PREFERENCES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<TablePreferencesRecord> getPrimaryKey() {
        return Keys.TABLE_PREFERENCES_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<TablePreferencesRecord>> getKeys() {
        return Arrays.<UniqueKey<TablePreferencesRecord>>asList(Keys.TABLE_PREFERENCES_PKEY, Keys.TABLE_PREFERENCES_DATABASE_TABLE_NAME_WITH_SCHEMA_IS_DEFAUL_KEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TablePreferencesTable as(String alias) {
        return new TablePreferencesTable(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TablePreferencesTable as(Name alias) {
        return new TablePreferencesTable(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public TablePreferencesTable rename(String name) {
        return new TablePreferencesTable(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public TablePreferencesTable rename(Name name) {
        return new TablePreferencesTable(name, null);
    }
}
