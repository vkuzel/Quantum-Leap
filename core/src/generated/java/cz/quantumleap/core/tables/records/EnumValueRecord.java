/*
 * This file is generated by jOOQ.
 */
package cz.quantumleap.core.tables.records;


import cz.quantumleap.core.tables.EnumValueTable;
import org.jooq.Record2;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class EnumValueRecord extends UpdatableRecordImpl<EnumValueRecord> {

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
        resetChangedOnNotNull();
    }
}
