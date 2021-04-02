package cz.quantumleap.core.common;

import cz.quantumleap.core.database.entity.EntityIdentifier;
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
        LocalDate start = LocalDate.parse("2020-05-01");
        LocalDate end = LocalDate.parse("2020-05-10");

        List<LocalDate> days = Utils.generateDaysBetween(start, end);

        Assertions.assertEquals(10, days.size());
        Assertions.assertTrue(days.contains(start));
        Assertions.assertTrue(days.contains(end));
    }

    @Test
    void checkTableTypeThrowsExceptionForIdentifierWithDifferentType() {
        EntityIdentifier<PersonTable> identifier = EntityIdentifier.forTable(PERSON);

        Assertions.assertThrows(IllegalStateException.class, () -> Utils.checkTableType(identifier, RoleTable.class));
    }

    @Test
    void checkTableTypeDoesNotFailForNullType() {
        EntityIdentifier<PersonTable> identifier = EntityIdentifier.forTable(PERSON);

        EntityIdentifier<?> checkedIdentifier = Utils.checkTableType(identifier, null);

        Assertions.assertNotNull(checkedIdentifier);
    }
}
