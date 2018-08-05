/*
 * This file is generated by jOOQ.
*/
package cz.quantumleap.core;


import javax.annotation.Generated;

import org.jooq.Sequence;
import org.jooq.impl.SequenceImpl;


/**
 * Convenience access to all sequences in core
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.7"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Sequences {

    /**
     * The sequence <code>core.increment_id_seq</code>
     */
    public static final Sequence<Long> INCREMENT_ID_SEQ = new SequenceImpl<Long>("increment_id_seq", Core.CORE, org.jooq.impl.SQLDataType.BIGINT.nullable(false));

    /**
     * The sequence <code>core.person_id_seq</code>
     */
    public static final Sequence<Long> PERSON_ID_SEQ = new SequenceImpl<Long>("person_id_seq", Core.CORE, org.jooq.impl.SQLDataType.BIGINT.nullable(false));

    /**
     * The sequence <code>core.person_role_id_seq</code>
     */
    public static final Sequence<Long> PERSON_ROLE_ID_SEQ = new SequenceImpl<Long>("person_role_id_seq", Core.CORE, org.jooq.impl.SQLDataType.BIGINT.nullable(false));

    /**
     * The sequence <code>core.role_id_seq</code>
     */
    public static final Sequence<Long> ROLE_ID_SEQ = new SequenceImpl<Long>("role_id_seq", Core.CORE, org.jooq.impl.SQLDataType.BIGINT.nullable(false));

    /**
     * The sequence <code>core.table_preferences_id_seq</code>
     */
    public static final Sequence<Long> TABLE_PREFERENCES_ID_SEQ = new SequenceImpl<Long>("table_preferences_id_seq", Core.CORE, org.jooq.impl.SQLDataType.BIGINT.nullable(false));
}
