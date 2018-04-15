/*
 * This file is generated by jOOQ.
*/
package cz.quantumleap.core;


import cz.quantumleap.core.tables.*;
import cz.quantumleap.core.tables.records.*;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.UniqueKey;
import org.jooq.impl.AbstractKeys;

import javax.annotation.Generated;


/**
 * A class modelling foreign key relationships between tables of the <code>core</code> 
 * schema
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.9.6"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // IDENTITY definitions
    // -------------------------------------------------------------------------

    public static final Identity<IncrementRecord, Long> IDENTITY_INCREMENT = Identities0.IDENTITY_INCREMENT;
    public static final Identity<PersonRecord, Long> IDENTITY_PERSON = Identities0.IDENTITY_PERSON;
    public static final Identity<PersonRoleRecord, Long> IDENTITY_PERSON_ROLE = Identities0.IDENTITY_PERSON_ROLE;
    public static final Identity<RoleRecord, Long> IDENTITY_ROLE = Identities0.IDENTITY_ROLE;
    public static final Identity<TablePreferencesRecord, Long> IDENTITY_TABLE_PREFERENCES = Identities0.IDENTITY_TABLE_PREFERENCES;

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<EnumRecord> ENUM_PKEY = UniqueKeys0.ENUM_PKEY;
    public static final UniqueKey<EnumValueRecord> ENUM_VALUE_PKEY = UniqueKeys0.ENUM_VALUE_PKEY;
    public static final UniqueKey<EnumValueRecord> ENUM_VALUE_ID_ENUM_ID_KEY = UniqueKeys0.ENUM_VALUE_ID_ENUM_ID_KEY;
    public static final UniqueKey<IncrementRecord> INCREMENT_PKEY = UniqueKeys0.INCREMENT_PKEY;
    public static final UniqueKey<PersonRecord> PERSON_PKEY = UniqueKeys0.PERSON_PKEY;
    public static final UniqueKey<PersonRecord> PERSON_EMAIL_KEY = UniqueKeys0.PERSON_EMAIL_KEY;
    public static final UniqueKey<PersonRoleRecord> PERSON_ROLE_PKEY = UniqueKeys0.PERSON_ROLE_PKEY;
    public static final UniqueKey<PersonRoleRecord> PERSON_ROLE_PERSON_ID_ROLE_ID_KEY = UniqueKeys0.PERSON_ROLE_PERSON_ID_ROLE_ID_KEY;
    public static final UniqueKey<RoleRecord> ROLE_PKEY = UniqueKeys0.ROLE_PKEY;
    public static final UniqueKey<RoleRecord> ROLE_NAME_KEY = UniqueKeys0.ROLE_NAME_KEY;
    public static final UniqueKey<TablePreferencesRecord> TABLE_PREFERENCES_PKEY = UniqueKeys0.TABLE_PREFERENCES_PKEY;
    public static final UniqueKey<TablePreferencesRecord> TABLE_PREFERENCES_DATABASE_TABLE_NAME_WITH_SCHEMA_IS_DEFAUL_KEY = UniqueKeys0.TABLE_PREFERENCES_DATABASE_TABLE_NAME_WITH_SCHEMA_IS_DEFAUL_KEY;

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------

    public static final ForeignKey<EnumValueRecord, EnumRecord> ENUM_VALUE__ENUM_VALUE_ENUM_ID_FKEY = ForeignKeys0.ENUM_VALUE__ENUM_VALUE_ENUM_ID_FKEY;
    public static final ForeignKey<PersonRoleRecord, PersonRecord> PERSON_ROLE__PERSON_ROLE_PERSON_ID_FKEY = ForeignKeys0.PERSON_ROLE__PERSON_ROLE_PERSON_ID_FKEY;
    public static final ForeignKey<PersonRoleRecord, RoleRecord> PERSON_ROLE__PERSON_ROLE_ROLE_ID_FKEY = ForeignKeys0.PERSON_ROLE__PERSON_ROLE_ROLE_ID_FKEY;

    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Identities0 extends AbstractKeys {
        public static Identity<IncrementRecord, Long> IDENTITY_INCREMENT = createIdentity(IncrementTable.INCREMENT, IncrementTable.INCREMENT.ID);
        public static Identity<PersonRecord, Long> IDENTITY_PERSON = createIdentity(PersonTable.PERSON, PersonTable.PERSON.ID);
        public static Identity<PersonRoleRecord, Long> IDENTITY_PERSON_ROLE = createIdentity(PersonRoleTable.PERSON_ROLE, PersonRoleTable.PERSON_ROLE.ID);
        public static Identity<RoleRecord, Long> IDENTITY_ROLE = createIdentity(RoleTable.ROLE, RoleTable.ROLE.ID);
        public static Identity<TablePreferencesRecord, Long> IDENTITY_TABLE_PREFERENCES = createIdentity(TablePreferencesTable.TABLE_PREFERENCES, TablePreferencesTable.TABLE_PREFERENCES.ID);
    }

    private static class UniqueKeys0 extends AbstractKeys {
        public static final UniqueKey<EnumRecord> ENUM_PKEY = createUniqueKey(EnumTable.ENUM, "enum_pkey", EnumTable.ENUM.ID);
        public static final UniqueKey<EnumValueRecord> ENUM_VALUE_PKEY = createUniqueKey(EnumValueTable.ENUM_VALUE, "enum_value_pkey", EnumValueTable.ENUM_VALUE.ID);
        public static final UniqueKey<EnumValueRecord> ENUM_VALUE_ID_ENUM_ID_KEY = createUniqueKey(EnumValueTable.ENUM_VALUE, "enum_value_id_enum_id_key", EnumValueTable.ENUM_VALUE.ID, EnumValueTable.ENUM_VALUE.ENUM_ID);
        public static final UniqueKey<IncrementRecord> INCREMENT_PKEY = createUniqueKey(IncrementTable.INCREMENT, "increment_pkey", IncrementTable.INCREMENT.ID);
        public static final UniqueKey<PersonRecord> PERSON_PKEY = createUniqueKey(PersonTable.PERSON, "person_pkey", PersonTable.PERSON.ID);
        public static final UniqueKey<PersonRecord> PERSON_EMAIL_KEY = createUniqueKey(PersonTable.PERSON, "person_email_key", PersonTable.PERSON.EMAIL);
        public static final UniqueKey<PersonRoleRecord> PERSON_ROLE_PKEY = createUniqueKey(PersonRoleTable.PERSON_ROLE, "person_role_pkey", PersonRoleTable.PERSON_ROLE.ID);
        public static final UniqueKey<PersonRoleRecord> PERSON_ROLE_PERSON_ID_ROLE_ID_KEY = createUniqueKey(PersonRoleTable.PERSON_ROLE, "person_role_person_id_role_id_key", PersonRoleTable.PERSON_ROLE.PERSON_ID, PersonRoleTable.PERSON_ROLE.ROLE_ID);
        public static final UniqueKey<RoleRecord> ROLE_PKEY = createUniqueKey(RoleTable.ROLE, "role_pkey", RoleTable.ROLE.ID);
        public static final UniqueKey<RoleRecord> ROLE_NAME_KEY = createUniqueKey(RoleTable.ROLE, "role_name_key", RoleTable.ROLE.NAME);
        public static final UniqueKey<TablePreferencesRecord> TABLE_PREFERENCES_PKEY = createUniqueKey(TablePreferencesTable.TABLE_PREFERENCES, "table_preferences_pkey", TablePreferencesTable.TABLE_PREFERENCES.ID);
        public static final UniqueKey<TablePreferencesRecord> TABLE_PREFERENCES_DATABASE_TABLE_NAME_WITH_SCHEMA_IS_DEFAUL_KEY = createUniqueKey(TablePreferencesTable.TABLE_PREFERENCES, "table_preferences_database_table_name_with_schema_is_defaul_key", TablePreferencesTable.TABLE_PREFERENCES.DATABASE_TABLE_NAME_WITH_SCHEMA, TablePreferencesTable.TABLE_PREFERENCES.IS_DEFAULT);
    }

    private static class ForeignKeys0 extends AbstractKeys {
        public static final ForeignKey<EnumValueRecord, EnumRecord> ENUM_VALUE__ENUM_VALUE_ENUM_ID_FKEY = createForeignKey(cz.quantumleap.core.Keys.ENUM_PKEY, EnumValueTable.ENUM_VALUE, "enum_value__enum_value_enum_id_fkey", EnumValueTable.ENUM_VALUE.ENUM_ID);
        public static final ForeignKey<PersonRoleRecord, PersonRecord> PERSON_ROLE__PERSON_ROLE_PERSON_ID_FKEY = createForeignKey(cz.quantumleap.core.Keys.PERSON_PKEY, PersonRoleTable.PERSON_ROLE, "person_role__person_role_person_id_fkey", PersonRoleTable.PERSON_ROLE.PERSON_ID);
        public static final ForeignKey<PersonRoleRecord, RoleRecord> PERSON_ROLE__PERSON_ROLE_ROLE_ID_FKEY = createForeignKey(cz.quantumleap.core.Keys.ROLE_PKEY, PersonRoleTable.PERSON_ROLE, "person_role__person_role_role_id_fkey", PersonRoleTable.PERSON_ROLE.ROLE_ID);
    }
}