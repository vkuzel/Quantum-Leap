package cz.quantumleap.core.database;

import cz.quantumleap.core.test.CoreSpringBootTest;
import org.jooq.DSLContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.ParameterizedTest.INDEX_PLACEHOLDER;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@CoreSpringBootTest
public class DatabaseFunctionTest {

    @Autowired
    private DSLContext dslContext;

    @ParameterizedTest(name = "generateIntervals[" + INDEX_PLACEHOLDER + "] for: [{0}, {1}], step={2} " +
            "expected: number of intervals={3}, first interval [{4}, {5}], last interval [{6}, {7}]")
    @MethodSource("generateIntervalsArguments")
    void generateIntervals(
            LocalDate start,
            LocalDate end,
            String step,
            int expectedNumberOfIntervals,
            Timestamp expectedFirstIntervalStart,
            Timestamp expectedFirstIntervalEnd,
            Timestamp expectedLastIntervalStart,
            Timestamp expectedLastIntervalEnd
    ) {
        String sql = "SELECT * FROM core.generate_intervals(? :: DATE, ? :: DATE, ?)";

        List<Map<String, Object>> rows = dslContext.fetch(sql, start, end, step).intoMaps();

        assertEquals(expectedNumberOfIntervals, rows.size());
        Map<String, Object> firstInterval = rows.get(0);
        assertEquals(expectedFirstIntervalStart, firstInterval.get("interval_start"));
        assertEquals(expectedFirstIntervalEnd, firstInterval.get("interval_end"));
        Map<String, Object> lastInterval = rows.get(rows.size() - 1);
        assertEquals(expectedLastIntervalStart, lastInterval.get("interval_start"));
        assertEquals(expectedLastIntervalEnd, lastInterval.get("interval_end"));
    }

    static Stream<Arguments> generateIntervalsArguments() {
        return Stream.of(
                createGenerateIntervalsArguments(
                        "2000-01-01",
                        "2000-01-31",
                        "DAY",
                        31,
                        "2000-01-01 00:00:00",
                        "2000-01-01 23:59:59.999999",
                        "2000-01-31 00:00:00",
                        "2000-01-31 23:59:59.999999"
                ),
                createGenerateIntervalsArguments(
                        "2000-01-01",
                        "2000-01-31",
                        "WEEK",
                        5,
                        "1999-12-27 00:00:00",
                        "2000-01-02 23:59:59.999999",
                        "2000-01-24 00:00:00",
                        "2000-01-30 23:59:59.999999"
                ),
                createGenerateIntervalsArguments(
                        "2000-01-01",
                        "2000-12-01",
                        "MONTH",
                        12,
                        "2000-01-01 00:00:00",
                        "2000-01-31 23:59:59.999999",
                        "2000-12-01 00:00:00",
                        "2000-12-31 23:59:59.999999"
                ),
                createGenerateIntervalsArguments(
                        "2000-01-01",
                        "2000-07-01",
                        "QUARTER",
                        3,
                        "2000-01-01 00:00:00",
                        "2000-03-31 23:59:59.999999",
                        "2000-07-01 00:00:00",
                        "2000-09-30 23:59:59.999999"
                ),
                createGenerateIntervalsArguments(
                        "2000-01-01",
                        "2003-01-01",
                        "YEAR",
                        4,
                        "2000-01-01 00:00:00",
                        "2000-12-31 23:59:59.999999",
                        "2003-01-01 00:00:00",
                        "2003-12-31 23:59:59.999999"
                )
        );
    }

    private static Arguments createGenerateIntervalsArguments(
            String start,
            String end,
            String step,
            int expectedNumberOfIntervals,
            String expectedFirstIntervalStart,
            String expectedFirstIntervalEnd,
            String expectedLastIntervalStart,
            String expectedLastIntervalEnd
    ) {
        return arguments(
                LocalDate.parse(start),
                LocalDate.parse(end),
                step,
                expectedNumberOfIntervals,
                Timestamp.valueOf(expectedFirstIntervalStart),
                Timestamp.valueOf(expectedFirstIntervalEnd),
                Timestamp.valueOf(expectedLastIntervalStart),
                Timestamp.valueOf(expectedLastIntervalEnd)
        );
    }
}
