/*
 * This file is generated by jOOQ.
*/
package cz.quantumleap.core;


import cz.quantumleap.core.tables.*;
import org.jooq.Catalog;
import org.jooq.Sequence;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;

import javax.annotation.Generated;
import java.util.ArrayList;
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
public class Core extends SchemaImpl {

    private static final long serialVersionUID = -978839602;

    /**
     * The reference instance of <code>core</code>
     */
    public static final Core CORE = new Core();

    /**
     * The table <code>core.enum</code>.
     */
    public final EnumTable ENUM = cz.quantumleap.core.tables.EnumTable.ENUM;

    /**
     * The table <code>core.enum_value</code>.
     */
    public final EnumValueTable ENUM_VALUE = cz.quantumleap.core.tables.EnumValueTable.ENUM_VALUE;

    /**
     * The table <code>core.increment</code>.
     */
    public final IncrementTable INCREMENT = cz.quantumleap.core.tables.IncrementTable.INCREMENT;

    /**
     * The table <code>core.person</code>.
     */
    public final PersonTable PERSON = cz.quantumleap.core.tables.PersonTable.PERSON;

    /**
     * The table <code>core.person_role</code>.
     */
    public final PersonRoleTable PERSON_ROLE = cz.quantumleap.core.tables.PersonRoleTable.PERSON_ROLE;

    /**
     * The table <code>core.role</code>.
     */
    public final RoleTable ROLE = cz.quantumleap.core.tables.RoleTable.ROLE;

    /**
     * The table <code>core.table_preferences</code>.
     */
    public final TablePreferencesTable TABLE_PREFERENCES = cz.quantumleap.core.tables.TablePreferencesTable.TABLE_PREFERENCES;

    /**
     * No further instances allowed
     */
    private Core() {
        super("core", null);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Sequence<?>> getSequences() {
        List result = new ArrayList();
        result.addAll(getSequences0());
        return result;
    }

    private final List<Sequence<?>> getSequences0() {
        return Arrays.<Sequence<?>>asList(
            Sequences.INCREMENT_ID_SEQ,
            Sequences.PERSON_ID_SEQ,
            Sequences.PERSON_ROLE_ID_SEQ,
            Sequences.ROLE_ID_SEQ,
            Sequences.TABLE_PREFERENCES_ID_SEQ);
    }

    @Override
    public final List<Table<?>> getTables() {
        List result = new ArrayList();
        result.addAll(getTables0());
        return result;
    }

    private final List<Table<?>> getTables0() {
        return Arrays.<Table<?>>asList(
            EnumTable.ENUM,
            EnumValueTable.ENUM_VALUE,
            IncrementTable.INCREMENT,
            PersonTable.PERSON,
            PersonRoleTable.PERSON_ROLE,
            RoleTable.ROLE,
            TablePreferencesTable.TABLE_PREFERENCES);
    }
}
