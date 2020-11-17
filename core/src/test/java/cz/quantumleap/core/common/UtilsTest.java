package cz.quantumleap.core.common;

import cz.quantumleap.core.data.entity.EntityIdentifier;
import cz.quantumleap.core.tables.PersonTable;
import cz.quantumleap.core.tables.RoleTable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static cz.quantumleap.core.tables.PersonTable.PERSON;

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

    @Test
    void checkTableTypeThrowsExceptionForIdentifierWithDifferentType() {
        // given
        EntityIdentifier<PersonTable> identifier = EntityIdentifier.forTable(PERSON);

        // when, then
        Assertions.assertThrows(IllegalStateException.class, () -> Utils.checkTableType(identifier, RoleTable.class));
    }

    @Test
    void checkTableTypeDoesNotFailForNullType() {
        // given
        EntityIdentifier<PersonTable> identifier = EntityIdentifier.forTable(PERSON);

        // when
        EntityIdentifier<?> checkedIdentifier = Utils.checkTableType(identifier, null);

        // then
        Assertions.assertNotNull(checkedIdentifier);
    }
}
