package cz.quantumleap.core.common;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

class UtilsTest {

    @Test
    void generatedDatesIncludeStartAndEndDay() {
        // given
        LocalDate start = LocalDate.parse("2020-05-01");
        LocalDate end = LocalDate.parse("2020-05-10");

        // when
        List<LocalDate> days = Utils.generateDaysBetween(start, end);

        // then
        Assertions.assertEquals(10, days.size());
        Assertions.assertTrue(days.contains(start));
        Assertions.assertTrue(days.contains(end));
    }
}
