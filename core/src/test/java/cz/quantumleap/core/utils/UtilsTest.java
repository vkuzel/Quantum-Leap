package cz.quantumleap.core.utils;

import cz.quantumleap.core.database.entity.EntityIdentifier;
import cz.quantumleap.core.tables.RoleTable;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static cz.quantumleap.core.tables.PersonTable.PERSON;
import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    @Test
    void generatedDatesIncludeStartAndEndDay() {
        var start = LocalDate.parse("2020-05-01");
        var end = LocalDate.parse("2020-05-10");

        var days = Utils.generateDaysBetween(start, end);

        assertEquals(10, days.size());
        assertTrue(days.contains(start));
        assertTrue(days.contains(end));
    }

    @Test
    void checkTableTypeThrowsExceptionForIdentifierWithDifferentType() {
        var identifier = EntityIdentifier.forTable(PERSON);

        assertThrows(IllegalStateException.class, () -> Utils.checkTableType(identifier, RoleTable.class));
    }

    @Test
    void checkTableTypeDoesNotFailForNullType() {
        var identifier = EntityIdentifier.forTable(PERSON);

        EntityIdentifier<?> checkedIdentifier = Utils.checkTableType(identifier, null);

        assertNotNull(checkedIdentifier);
    }

    @Test
    void min() {
        var one = Integer.valueOf(1);
        var two = Integer.valueOf(2);
        var three = Integer.valueOf(3);

        var min = Utils.min(one, two, three, null);

        assertEquals(one, min);
    }

    @Test
    void max() {
        var one = Integer.valueOf(1);
        var two = Integer.valueOf(2);
        var three = Integer.valueOf(3);

        var max = Utils.max(one, two, three, null);

        assertEquals(three, max);
    }
}
