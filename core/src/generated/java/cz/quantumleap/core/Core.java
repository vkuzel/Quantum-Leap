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
import java.util.Arrays;
import java.util.List;

import org.jooq.Catalog;
import org.jooq.Configuration;
import org.jooq.Field;
import org.jooq.Result;
import org.jooq.Sequence;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Core extends SchemaImpl {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>core</code>
     */
    public static final Core CORE = new Core();

    /**
     * The table <code>core.enum</code>.
     */
    public final EnumTable ENUM = EnumTable.ENUM;

    /**
     * The table <code>core.enum_value</code>.
     */
    public final EnumValueTable ENUM_VALUE = EnumValueTable.ENUM_VALUE;

    /**
     * The table <code>core.generate_intervals</code>.
     */
    public final GenerateIntervalsTable GENERATE_INTERVALS = GenerateIntervalsTable.GENERATE_INTERVALS;

    /**
     * Call <code>core.generate_intervals</code>.
     */
    public static Result<GenerateIntervalsRecord> GENERATE_INTERVALS(
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
    public static GenerateIntervalsTable GENERATE_INTERVALS(
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
    public static GenerateIntervalsTable GENERATE_INTERVALS(
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

    /**
     * The table <code>core.increment</code>.
     */
    public final IncrementTable INCREMENT = IncrementTable.INCREMENT;

    /**
     * The table <code>core.notification</code>.
     */
    public final NotificationTable NOTIFICATION = NotificationTable.NOTIFICATION;

    /**
     * The table <code>core.person</code>.
     */
    public final PersonTable PERSON = PersonTable.PERSON;

    /**
     * The table <code>core.person_role</code>.
     */
    public final PersonRoleTable PERSON_ROLE = PersonRoleTable.PERSON_ROLE;

    /**
     * The table <code>core.role</code>.
     */
    public final RoleTable ROLE = RoleTable.ROLE;

    /**
     * The table <code>core.slice_query</code>.
     */
    public final SliceQueryTable SLICE_QUERY = SliceQueryTable.SLICE_QUERY;

    /**
     * No further instances allowed
     */
    private Core() {
        super("core", null);
    }


    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Sequence<?>> getSequences() {
        return Arrays.<Sequence<?>>asList(
            Sequences.INCREMENT_ID_SEQ,
            Sequences.NOTIFICATION_ID_SEQ,
            Sequences.PERSON_ID_SEQ,
            Sequences.PERSON_ROLE_ID_SEQ,
            Sequences.ROLE_ID_SEQ,
            Sequences.SLICE_QUERY_ID_SEQ);
    }

    @Override
    public final List<Table<?>> getTables() {
        return Arrays.<Table<?>>asList(
            EnumTable.ENUM,
            EnumValueTable.ENUM_VALUE,
            GenerateIntervalsTable.GENERATE_INTERVALS,
            IncrementTable.INCREMENT,
            NotificationTable.NOTIFICATION,
            PersonTable.PERSON,
            PersonRoleTable.PERSON_ROLE,
            RoleTable.ROLE,
            SliceQueryTable.SLICE_QUERY);
    }
}
