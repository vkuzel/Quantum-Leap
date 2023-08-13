package cz.quantumleap.core.common;

import cz.quantumleap.core.database.entity.EntityIdentifier;
import cz.quantumleap.core.tables.RoleTable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static cz.quantumleap.core.tables.PersonTable.PERSON;

class UtilsTest {

    @Test
    void generatedDatesIncludeStartAndEndDay() {
        var start = LocalDate.parse("2020-05-01");
        var end = LocalDate.parse("2020-05-10");

        var days = Utils.generateDaysBetween(start, end);

        Assertions.assertEquals(10, days.size());
        Assertions.assertTrue(days.contains(start));
        Assertions.assertTrue(days.contains(end));
    }

    @Test
    void checkTableTypeThrowsExceptionForIdentifierWithDifferentType() {
        var identifier = EntityIdentifier.forTable(PERSON);

        Assertions.assertThrows(IllegalStateException.class, () -> Utils.checkTableType(identifier, RoleTable.class));
    }

    @Test
    void checkTableTypeDoesNotFailForNullType() {
        var identifier = EntityIdentifier.forTable(PERSON);

        EntityIdentifier<?> checkedIdentifier = Utils.checkTableType(identifier, null);

        Assertions.assertNotNull(checkedIdentifier);
    }
}
