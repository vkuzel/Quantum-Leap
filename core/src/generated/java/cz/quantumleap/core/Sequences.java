/*
 * This file is generated by jOOQ.
 */
package cz.quantumleap.core;


import org.jooq.Sequence;
import org.jooq.impl.Internal;


/**
 * Convenience access to all sequences in core
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Sequences {

    /**
     * The sequence <code>core.increment_id_seq</code>
     */
    public static final Sequence<Long> INCREMENT_ID_SEQ = Internal.createSequence("increment_id_seq", Core.CORE, org.jooq.impl.SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>core.person_id_seq</code>
     */
    public static final Sequence<Long> PERSON_ID_SEQ = Internal.createSequence("person_id_seq", Core.CORE, org.jooq.impl.SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>core.person_role_id_seq</code>
     */
    public static final Sequence<Long> PERSON_ROLE_ID_SEQ = Internal.createSequence("person_role_id_seq", Core.CORE, org.jooq.impl.SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>core.role_id_seq</code>
     */
    public static final Sequence<Long> ROLE_ID_SEQ = Internal.createSequence("role_id_seq", Core.CORE, org.jooq.impl.SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>core.table_preferences_id_seq</code>
     */
    public static final Sequence<Long> TABLE_PREFERENCES_ID_SEQ = Internal.createSequence("table_preferences_id_seq", Core.CORE, org.jooq.impl.SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);
}
