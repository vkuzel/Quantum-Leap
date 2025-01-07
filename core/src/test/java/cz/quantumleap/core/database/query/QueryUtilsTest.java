package cz.quantumleap.core.database.query;

import org.jooq.impl.DSL;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QueryUtilsTest {

    @ParameterizedTest
    @CsvSource({
            "field-name, field-name_lookup",
            "field-name_id, field-name",
    })
    void resolveLookupFieldName(String fieldName, String expectedName) {
        var field = DSL.field(DSL.name(fieldName));

        var name = QueryUtils.resolveLookupFieldName(field);

        assertEquals(expectedName, name);
    }
}