/*
 * This file is generated by jOOQ.
*/
package cz.quantumleap.core.tables;


import cz.quantumleap.core.Core;
import cz.quantumleap.core.Keys;
import cz.quantumleap.core.persistence.converter.LocalDateTimeConverter;
import cz.quantumleap.core.tables.records.PersonRecord;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Identity;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.9.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Person extends TableImpl<PersonRecord> {

    private static final long serialVersionUID = -492989673;

    /**
     * The reference instance of <code>core.person</code>
     */
    public static final Person PERSON = new Person();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<PersonRecord> getRecordType() {
        return PersonRecord.class;
    }

    /**
     * The column <code>core.person.id</code>.
     */
    public final TableField<PersonRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('core.person_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>core.person.email</code>.
     */
    public final TableField<PersonRecord, String> EMAIL = createField("email", org.jooq.impl.SQLDataType.VARCHAR.length(255).nullable(false), this, "");

    /**
     * The column <code>core.person.created_at</code>.
     */
    public final TableField<PersonRecord, LocalDateTime> CREATED_AT = createField("created_at", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "", new LocalDateTimeConverter());

    /**
     * Create a <code>core.person</code> table reference
     */
    public Person() {
        this("person", null);
    }

    /**
     * Create an aliased <code>core.person</code> table reference
     */
    public Person(String alias) {
        this(alias, PERSON);
    }

    private Person(String alias, Table<PersonRecord> aliased) {
        this(alias, aliased, null);
    }

    private Person(String alias, Table<PersonRecord> aliased, Field<?>[] parameters) {
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
    public Identity<PersonRecord, Long> getIdentity() {
        return Keys.IDENTITY_PERSON;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<PersonRecord> getPrimaryKey() {
        return Keys.PERSON_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<PersonRecord>> getKeys() {
        return Arrays.<UniqueKey<PersonRecord>>asList(Keys.PERSON_PKEY, Keys.PERSON_EMAIL_KEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Person as(String alias) {
        return new Person(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Person rename(String name) {
        return new Person(name, null);
    }
}