/*
 * This file is generated by jOOQ.
 */
package cz.quantumleap.core.tables.records;


import cz.quantumleap.core.tables.EnumValueTable;

import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class EnumValueRecord extends UpdatableRecordImpl<EnumValueRecord> implements Record3<String, String, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>core.enum_value.id</code>.
     */
    public void setId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>core.enum_value.id</code>.
     */
    public String getId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>core.enum_value.enum_id</code>.
     */
    public void setEnumId(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>core.enum_value.enum_id</code>.
     */
    public String getEnumId() {
        return (String) get(1);
    }

    /**
     * Setter for <code>core.enum_value.label</code>.
     */
    public void setLabel(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>core.enum_value.label</code>.
     */
    public String getLabel() {
        return (String) get(2);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record2<String, String> key() {
        return (Record2) super.key();
    }

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row3<String, String, String> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    @Override
    public Row3<String, String, String> valuesRow() {
        return (Row3) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return EnumValueTable.ENUM_VALUE.ID;
    }

    @Override
    public Field<String> field2() {
        return EnumValueTable.ENUM_VALUE.ENUM_ID;
    }

    @Override
    public Field<String> field3() {
        return EnumValueTable.ENUM_VALUE.LABEL;
    }

    @Override
    public String component1() {
        return getId();
    }

    @Override
    public String component2() {
        return getEnumId();
    }

    @Override
    public String component3() {
        return getLabel();
    }

    @Override
    public String value1() {
        return getId();
    }

    @Override
    public String value2() {
        return getEnumId();
    }

    @Override
    public String value3() {
        return getLabel();
    }

    @Override
    public EnumValueRecord value1(String value) {
        setId(value);
        return this;
    }

    @Override
    public EnumValueRecord value2(String value) {
        setEnumId(value);
        return this;
    }

    @Override
    public EnumValueRecord value3(String value) {
        setLabel(value);
        return this;
    }

    @Override
    public EnumValueRecord values(String value1, String value2, String value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached EnumValueRecord
     */
    public EnumValueRecord() {
        super(EnumValueTable.ENUM_VALUE);
    }

    /**
     * Create a detached, initialised EnumValueRecord
     */
    public EnumValueRecord(String id, String enumId, String label) {
        super(EnumValueTable.ENUM_VALUE);

        setId(id);
        setEnumId(enumId);
        setLabel(label);
    }
}
