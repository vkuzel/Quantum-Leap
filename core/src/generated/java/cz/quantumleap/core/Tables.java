/*
 * This file is generated by jOOQ.
*/
package cz.quantumleap.core;


import cz.quantumleap.core.tables.EnumTable;
import cz.quantumleap.core.tables.EnumValueTable;
import cz.quantumleap.core.tables.IncrementTable;
import cz.quantumleap.core.tables.PersonRoleTable;
import cz.quantumleap.core.tables.PersonTable;
import cz.quantumleap.core.tables.RoleTable;
import cz.quantumleap.core.tables.TablePreferencesTable;

import javax.annotation.Generated;


/**
 * Convenience access to all tables in core
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.7"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Tables {

    /**
     * The table <code>core.enum</code>.
     */
    public static final EnumTable ENUM = cz.quantumleap.core.tables.EnumTable.ENUM;

    /**
     * The table <code>core.enum_value</code>.
     */
    public static final EnumValueTable ENUM_VALUE = cz.quantumleap.core.tables.EnumValueTable.ENUM_VALUE;

    /**
     * The table <code>core.increment</code>.
     */
    public static final IncrementTable INCREMENT = cz.quantumleap.core.tables.IncrementTable.INCREMENT;

    /**
     * The table <code>core.person</code>.
     */
    public static final PersonTable PERSON = cz.quantumleap.core.tables.PersonTable.PERSON;

    /**
     * The table <code>core.person_role</code>.
     */
    public static final PersonRoleTable PERSON_ROLE = cz.quantumleap.core.tables.PersonRoleTable.PERSON_ROLE;

    /**
     * The table <code>core.role</code>.
     */
    public static final RoleTable ROLE = cz.quantumleap.core.tables.RoleTable.ROLE;

    /**
     * The table <code>core.table_preferences</code>.
     */
    public static final TablePreferencesTable TABLE_PREFERENCES = cz.quantumleap.core.tables.TablePreferencesTable.TABLE_PREFERENCES;
}
