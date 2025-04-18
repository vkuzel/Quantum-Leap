/*
 * This file is generated by jOOQ.
 */
package cz.quantumleap.core;


import cz.quantumleap.core.tables.EnumTable;
import cz.quantumleap.core.tables.EnumValueTable;
import cz.quantumleap.core.tables.GenerateIntervalsTable;
import cz.quantumleap.core.tables.IncrementTable;
import cz.quantumleap.core.tables.NotificationTable;
import cz.quantumleap.core.tables.PersonRoleTable;
import cz.quantumleap.core.tables.PersonTable;
import cz.quantumleap.core.tables.RoleTable;
import cz.quantumleap.core.tables.SliceQueryTable;
import cz.quantumleap.core.tables.records.GenerateIntervalsRecord;

import java.time.LocalDate;

import org.jooq.Configuration;
import org.jooq.Field;
import org.jooq.Result;


/**
 * Convenience access to all tables in core.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Tables {

    /**
     * The table <code>core.enum</code>.
     */
    public static final EnumTable ENUM = EnumTable.ENUM;

    /**
     * The table <code>core.enum_value</code>.
     */
    public static final EnumValueTable ENUM_VALUE = EnumValueTable.ENUM_VALUE;

    /**
     * The table <code>core.generate_intervals</code>.
     */
    public static final GenerateIntervalsTable GENERATE_INTERVALS = GenerateIntervalsTable.GENERATE_INTERVALS;

    /**
     * Call <code>core.generate_intervals</code>.
     */
    public static Result<GenerateIntervalsRecord> GENERATE_INTERVALS(
          Configuration configuration
        , LocalDate intervalsStart
        , LocalDate intervalsEnd
        , String step
        , Boolean openEnd
    ) {
        return configuration.dsl().selectFrom(cz.quantumleap.core.tables.GenerateIntervalsTable.GENERATE_INTERVALS.call(
              intervalsStart
            , intervalsEnd
            , step
            , openEnd
        )).fetch();
    }

    /**
     * Get <code>core.generate_intervals</code> as a table.
     */
    public static GenerateIntervalsTable GENERATE_INTERVALS(
          LocalDate intervalsStart
        , LocalDate intervalsEnd
        , String step
        , Boolean openEnd
    ) {
        return cz.quantumleap.core.tables.GenerateIntervalsTable.GENERATE_INTERVALS.call(
            intervalsStart,
            intervalsEnd,
            step,
            openEnd
        );
    }

    /**
     * Get <code>core.generate_intervals</code> as a table.
     */
    public static GenerateIntervalsTable GENERATE_INTERVALS(
          Field<LocalDate> intervalsStart
        , Field<LocalDate> intervalsEnd
        , Field<String> step
        , Field<Boolean> openEnd
    ) {
        return cz.quantumleap.core.tables.GenerateIntervalsTable.GENERATE_INTERVALS.call(
            intervalsStart,
            intervalsEnd,
            step,
            openEnd
        );
    }

    /**
     * The table <code>core.increment</code>.
     */
    public static final IncrementTable INCREMENT = IncrementTable.INCREMENT;

    /**
     * The table <code>core.notification</code>.
     */
    public static final NotificationTable NOTIFICATION = NotificationTable.NOTIFICATION;

    /**
     * The table <code>core.person</code>.
     */
    public static final PersonTable PERSON = PersonTable.PERSON;

    /**
     * The table <code>core.person_role</code>.
     */
    public static final PersonRoleTable PERSON_ROLE = PersonRoleTable.PERSON_ROLE;

    /**
     * The table <code>core.role</code>.
     */
    public static final RoleTable ROLE = RoleTable.ROLE;

    /**
     * The table <code>core.slice_query</code>.
     */
    public static final SliceQueryTable SLICE_QUERY = SliceQueryTable.SLICE_QUERY;
}
