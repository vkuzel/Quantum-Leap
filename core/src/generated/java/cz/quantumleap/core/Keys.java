/*
 * This file is generated by jOOQ.
 */
package cz.quantumleap.core;


import cz.quantumleap.core.tables.EnumTable;
import cz.quantumleap.core.tables.EnumValueTable;
import cz.quantumleap.core.tables.IncrementTable;
import cz.quantumleap.core.tables.NotificationTable;
import cz.quantumleap.core.tables.PersonRoleTable;
import cz.quantumleap.core.tables.PersonTable;
import cz.quantumleap.core.tables.RoleTable;
import cz.quantumleap.core.tables.TablePreferencesTable;
import cz.quantumleap.core.tables.records.EnumRecord;
import cz.quantumleap.core.tables.records.EnumValueRecord;
import cz.quantumleap.core.tables.records.IncrementRecord;
import cz.quantumleap.core.tables.records.NotificationRecord;
import cz.quantumleap.core.tables.records.PersonRecord;
import cz.quantumleap.core.tables.records.PersonRoleRecord;
import cz.quantumleap.core.tables.records.RoleRecord;
import cz.quantumleap.core.tables.records.TablePreferencesRecord;

import org.jooq.ForeignKey;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables in 
 * core.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<EnumRecord> ENUM_PKEY = Internal.createUniqueKey(EnumTable.ENUM, DSL.name("enum_pkey"), new TableField[] { EnumTable.ENUM.ID }, true);
    public static final UniqueKey<EnumValueRecord> ENUM_VALUE_PKEY = Internal.createUniqueKey(EnumValueTable.ENUM_VALUE, DSL.name("enum_value_pkey"), new TableField[] { EnumValueTable.ENUM_VALUE.ID, EnumValueTable.ENUM_VALUE.ENUM_ID }, true);
    public static final UniqueKey<IncrementRecord> INCREMENT_PKEY = Internal.createUniqueKey(IncrementTable.INCREMENT, DSL.name("increment_pkey"), new TableField[] { IncrementTable.INCREMENT.ID }, true);
    public static final UniqueKey<NotificationRecord> NOTIFICATION_PKEY = Internal.createUniqueKey(NotificationTable.NOTIFICATION, DSL.name("notification_pkey"), new TableField[] { NotificationTable.NOTIFICATION.ID }, true);
    public static final UniqueKey<PersonRecord> PERSON_EMAIL_KEY = Internal.createUniqueKey(PersonTable.PERSON, DSL.name("person_email_key"), new TableField[] { PersonTable.PERSON.EMAIL }, true);
    public static final UniqueKey<PersonRecord> PERSON_PKEY = Internal.createUniqueKey(PersonTable.PERSON, DSL.name("person_pkey"), new TableField[] { PersonTable.PERSON.ID }, true);
    public static final UniqueKey<PersonRoleRecord> PERSON_ROLE_PERSON_ID_ROLE_ID_KEY = Internal.createUniqueKey(PersonRoleTable.PERSON_ROLE, DSL.name("person_role_person_id_role_id_key"), new TableField[] { PersonRoleTable.PERSON_ROLE.PERSON_ID, PersonRoleTable.PERSON_ROLE.ROLE_ID }, true);
    public static final UniqueKey<PersonRoleRecord> PERSON_ROLE_PKEY = Internal.createUniqueKey(PersonRoleTable.PERSON_ROLE, DSL.name("person_role_pkey"), new TableField[] { PersonRoleTable.PERSON_ROLE.ID }, true);
    public static final UniqueKey<RoleRecord> ROLE_NAME_KEY = Internal.createUniqueKey(RoleTable.ROLE, DSL.name("role_name_key"), new TableField[] { RoleTable.ROLE.NAME }, true);
    public static final UniqueKey<RoleRecord> ROLE_PKEY = Internal.createUniqueKey(RoleTable.ROLE, DSL.name("role_pkey"), new TableField[] { RoleTable.ROLE.ID }, true);
    public static final UniqueKey<TablePreferencesRecord> TABLE_PREFERENCES_ENTITY_IDENTIFIER_IS_DEFAULT_KEY = Internal.createUniqueKey(TablePreferencesTable.TABLE_PREFERENCES, DSL.name("table_preferences_entity_identifier_is_default_key"), new TableField[] { TablePreferencesTable.TABLE_PREFERENCES.ENTITY_IDENTIFIER, TablePreferencesTable.TABLE_PREFERENCES.IS_DEFAULT }, true);
    public static final UniqueKey<TablePreferencesRecord> TABLE_PREFERENCES_PKEY = Internal.createUniqueKey(TablePreferencesTable.TABLE_PREFERENCES, DSL.name("table_preferences_pkey"), new TableField[] { TablePreferencesTable.TABLE_PREFERENCES.ID }, true);

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------

    public static final ForeignKey<EnumValueRecord, EnumRecord> ENUM_VALUE__ENUM_VALUE_ENUM_ID_FKEY = Internal.createForeignKey(EnumValueTable.ENUM_VALUE, DSL.name("enum_value_enum_id_fkey"), new TableField[] { EnumValueTable.ENUM_VALUE.ENUM_ID }, Keys.ENUM_PKEY, new TableField[] { EnumTable.ENUM.ID }, true);
    public static final ForeignKey<NotificationRecord, PersonRecord> NOTIFICATION__NOTIFICATION_PERSON_ID_FKEY = Internal.createForeignKey(NotificationTable.NOTIFICATION, DSL.name("notification_person_id_fkey"), new TableField[] { NotificationTable.NOTIFICATION.PERSON_ID }, Keys.PERSON_PKEY, new TableField[] { PersonTable.PERSON.ID }, true);
    public static final ForeignKey<NotificationRecord, PersonRecord> NOTIFICATION__NOTIFICATION_RESOLVED_BY_FKEY = Internal.createForeignKey(NotificationTable.NOTIFICATION, DSL.name("notification_resolved_by_fkey"), new TableField[] { NotificationTable.NOTIFICATION.RESOLVED_BY }, Keys.PERSON_PKEY, new TableField[] { PersonTable.PERSON.ID }, true);
    public static final ForeignKey<NotificationRecord, RoleRecord> NOTIFICATION__NOTIFICATION_ROLE_ID_FKEY = Internal.createForeignKey(NotificationTable.NOTIFICATION, DSL.name("notification_role_id_fkey"), new TableField[] { NotificationTable.NOTIFICATION.ROLE_ID }, Keys.ROLE_PKEY, new TableField[] { RoleTable.ROLE.ID }, true);
    public static final ForeignKey<PersonRoleRecord, PersonRecord> PERSON_ROLE__PERSON_ROLE_PERSON_ID_FKEY = Internal.createForeignKey(PersonRoleTable.PERSON_ROLE, DSL.name("person_role_person_id_fkey"), new TableField[] { PersonRoleTable.PERSON_ROLE.PERSON_ID }, Keys.PERSON_PKEY, new TableField[] { PersonTable.PERSON.ID }, true);
    public static final ForeignKey<PersonRoleRecord, RoleRecord> PERSON_ROLE__PERSON_ROLE_ROLE_ID_FKEY = Internal.createForeignKey(PersonRoleTable.PERSON_ROLE, DSL.name("person_role_role_id_fkey"), new TableField[] { PersonRoleTable.PERSON_ROLE.ROLE_ID }, Keys.ROLE_PKEY, new TableField[] { RoleTable.ROLE.ID }, true);
}
