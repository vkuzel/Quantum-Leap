/*
 * This file is generated by jOOQ.
 */
package cz.quantumleap.core;


import cz.quantumleap.core.tables.GenerateIntervalsTable;
import cz.quantumleap.core.tables.records.GenerateIntervalsRecord;
import org.jooq.Configuration;
import org.jooq.Field;
import org.jooq.Result;

import java.time.LocalDate;


/**
 * Convenience access to all stored procedures and functions in core.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Routines {

    /**
     * Call <code>core.generate_intervals</code>.
     */
    public static Result<GenerateIntervalsRecord> generateIntervals(
          Configuration configuration
        , LocalDate intervalsStart
        , LocalDate intervalsEnd
        , String step
    ) {
        return configuration.dsl().selectFrom(cz.quantumleap.core.tables.GenerateIntervalsTable.GENERATE_INTERVALS.call(
              intervalsStart
            , intervalsEnd
            , step
        )).fetch();
    }

    /**
     * Get <code>core.generate_intervals</code> as a table.
     */
    public static GenerateIntervalsTable generateIntervals(
          LocalDate intervalsStart
        , LocalDate intervalsEnd
        , String step
    ) {
        return cz.quantumleap.core.tables.GenerateIntervalsTable.GENERATE_INTERVALS.call(
              intervalsStart
            , intervalsEnd
            , step
        );
    }

    /**
     * Get <code>core.generate_intervals</code> as a table.
     */
    public static GenerateIntervalsTable generateIntervals(
          Field<LocalDate> intervalsStart
        , Field<LocalDate> intervalsEnd
        , Field<String> step
    ) {
        return cz.quantumleap.core.tables.GenerateIntervalsTable.GENERATE_INTERVALS.call(
              intervalsStart
            , intervalsEnd
            , step
        );
    }
}