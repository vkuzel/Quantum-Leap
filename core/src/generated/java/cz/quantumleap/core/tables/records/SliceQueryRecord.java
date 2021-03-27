/*
 * This file is generated by jOOQ.
 */
package cz.quantumleap.core.tables.records;


import cz.quantumleap.core.tables.SliceQueryTable;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record6;
import org.jooq.Row6;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class SliceQueryRecord extends UpdatableRecordImpl<SliceQueryRecord> implements Record6<Long, String, Long, Boolean, String, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>core.slice_query.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>core.slice_query.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>core.slice_query.entity_identifier</code>.
     */
    public void setEntityIdentifier(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>core.slice_query.entity_identifier</code>.
     */
    public String getEntityIdentifier() {
        return (String) get(1);
    }

    /**
     * Setter for <code>core.slice_query.person_id</code>.
     */
    public void setPersonId(Long value) {
        set(2, value);
    }

    /**
     * Getter for <code>core.slice_query.person_id</code>.
     */
    public Long getPersonId() {
        return (Long) get(2);
    }

    /**
     * Setter for <code>core.slice_query.is_default</code>.
     */
    public void setIsDefault(Boolean value) {
        set(3, value);
    }

    /**
     * Getter for <code>core.slice_query.is_default</code>.
     */
    public Boolean getIsDefault() {
        return (Boolean) get(3);
    }

    /**
     * Setter for <code>core.slice_query.name</code>.
     */
    public void setName(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>core.slice_query.name</code>.
     */
    public String getName() {
        return (String) get(4);
    }

    /**
     * Setter for <code>core.slice_query.query</code>.
     */
    public void setQuery(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>core.slice_query.query</code>.
     */
    public String getQuery() {
        return (String) get(5);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record6 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row6<Long, String, Long, Boolean, String, String> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    @Override
    public Row6<Long, String, Long, Boolean, String, String> valuesRow() {
        return (Row6) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return SliceQueryTable.SLICE_QUERY.ID;
    }

    @Override
    public Field<String> field2() {
        return SliceQueryTable.SLICE_QUERY.ENTITY_IDENTIFIER;
    }

    @Override
    public Field<Long> field3() {
        return SliceQueryTable.SLICE_QUERY.PERSON_ID;
    }

    @Override
    public Field<Boolean> field4() {
        return SliceQueryTable.SLICE_QUERY.IS_DEFAULT;
    }

    @Override
    public Field<String> field5() {
        return SliceQueryTable.SLICE_QUERY.NAME;
    }

    @Override
    public Field<String> field6() {
        return SliceQueryTable.SLICE_QUERY.QUERY;
    }

    @Override
    public Long component1() {
        return getId();
    }

    @Override
    public String component2() {
        return getEntityIdentifier();
    }

    @Override
    public Long component3() {
        return getPersonId();
    }

    @Override
    public Boolean component4() {
        return getIsDefault();
    }

    @Override
    public String component5() {
        return getName();
    }

    @Override
    public String component6() {
        return getQuery();
    }

    @Override
    public Long value1() {
        return getId();
    }

    @Override
    public String value2() {
        return getEntityIdentifier();
    }

    @Override
    public Long value3() {
        return getPersonId();
    }

    @Override
    public Boolean value4() {
        return getIsDefault();
    }

    @Override
    public String value5() {
        return getName();
    }

    @Override
    public String value6() {
        return getQuery();
    }

    @Override
    public SliceQueryRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public SliceQueryRecord value2(String value) {
        setEntityIdentifier(value);
        return this;
    }

    @Override
    public SliceQueryRecord value3(Long value) {
        setPersonId(value);
        return this;
    }

    @Override
    public SliceQueryRecord value4(Boolean value) {
        setIsDefault(value);
        return this;
    }

    @Override
    public SliceQueryRecord value5(String value) {
        setName(value);
        return this;
    }

    @Override
    public SliceQueryRecord value6(String value) {
        setQuery(value);
        return this;
    }

    @Override
    public SliceQueryRecord values(Long value1, String value2, Long value3, Boolean value4, String value5, String value6) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached SliceQueryRecord
     */
    public SliceQueryRecord() {
        super(SliceQueryTable.SLICE_QUERY);
    }

    /**
     * Create a detached, initialised SliceQueryRecord
     */
    public SliceQueryRecord(Long id, String entityIdentifier, Long personId, Boolean isDefault, String name, String query) {
        super(SliceQueryTable.SLICE_QUERY);

        setId(id);
        setEntityIdentifier(entityIdentifier);
        setPersonId(personId);
        setIsDefault(isDefault);
        setName(name);
        setQuery(query);
    }
}
