package cz.quantumleap.core.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NumbersTest {

    @ParameterizedTest
    @CsvSource({
            "123, true",
            "-123.45, true",
            "000.00, true",
            ".123, true",
            "-.123, true",
            "123.45.67, false",
            "123., false",
            "-, false",
            "., false",
            "abc, false",
    })
    void isParsable(String text, boolean expected) {
        var parsable = Numbers.isParsable(text);

        assertEquals(expected, parsable, () -> "'%s' not evaluated properly".formatted(text));
    }
}