/*
 * This file is generated by jOOQ.
 */
package cz.quantumleap.core.tables.records;


import cz.quantumleap.core.tables.EnumTable;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class EnumRecord extends UpdatableRecordImpl<EnumRecord> implements Record2<String, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>core.enum.id</code>.
     */
    public void setId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>core.enum.id</code>.
     */
    public String getId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>core.enum.name</code>.
     */
    public void setName(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>core.enum.name</code>.
     */
    public String getName() {
        return (String) get(1);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record2 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row2<String, String> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    @Override
    public Row2<String, String> valuesRow() {
        return (Row2) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return EnumTable.ENUM.ID;
    }

    @Override
    public Field<String> field2() {
        return EnumTable.ENUM.NAME;
    }

    @Override
    public String component1() {
        return getId();
    }

    @Override
    public String component2() {
        return getName();
    }

    @Override
    public String value1() {
        return getId();
    }

    @Override
    public String value2() {
        return getName();
    }

    @Override
    public EnumRecord value1(String value) {
        setId(value);
        return this;
    }

    @Override
    public EnumRecord value2(String value) {
        setName(value);
        return this;
    }

    @Override
    public EnumRecord values(String value1, String value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached EnumRecord
     */
    public EnumRecord() {
        super(EnumTable.ENUM);
    }

    /**
     * Create a detached, initialised EnumRecord
     */
    public EnumRecord(String id, String name) {
        super(EnumTable.ENUM);

        setId(id);
        setName(name);
    }
}
