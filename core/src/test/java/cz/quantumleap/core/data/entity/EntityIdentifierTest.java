package cz.quantumleap.core.data.entity;

import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EntityIdentifierTest {

    @ParameterizedTest
    @MethodSource("provideArgumentsForSuccessfulParseTest")
    void parseParsesTextToEntityIdentifier(String text, EntityIdentifier<?> expected) {
        // when
        EntityIdentifier<?> identifier = EntityIdentifier.parse(text);

        // then
        assertEquals(expected, identifier);
    }

    private static Stream<Arguments> provideArgumentsForSuccessfulParseTest() {
        Table<?> table1 = createTable("schema", "table");
        Table<?> table2 = createTable("sch\\\"ema", "tab\\\"le");
        return Stream.of(
                Arguments.of("schema.table", EntityIdentifier.forTable(table1)),
                Arguments.of("\"schema\".\"table\"", EntityIdentifier.forTable(table1)),
                Arguments.of("\"sch\\\"ema\".\"tab\\\"le\"", EntityIdentifier.forTable(table2)),
                Arguments.of("schema.table#qualifier", EntityIdentifier.forTableWithQualifier(table1, "qualifier"))
        );
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsForIncorrectParseTest")
    void parseThrowsExceptionForIncorrectText(String text) {
        // when, then
        Assertions.assertThrows(IllegalArgumentException.class, () -> EntityIdentifier.parse(text));
    }

    private static Stream<Arguments> provideArgumentsForIncorrectParseTest() {
        return Stream.of(
                Arguments.of("table"),
                Arguments.of("database.schema.table#qualifier")
        );
    }

    private static Table<? extends Record> createTable(String schemaName, String tableName) {
        Name name = DSL.name(schemaName, tableName);
        return DSL.table(name);
    }
}
