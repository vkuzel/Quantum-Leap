package cz.quantumleap.core.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static java.util.function.Function.identity;
import static org.junit.jupiter.api.Assertions.assertEquals;

class StringsTest {

    @ParameterizedTest
    @CsvSource(value = {
            "BRAVE_NEW_WORLD, brave-new-world",
            "H, h",
            "'', ''",
            "null, null"
    }, nullValues = "null")
    void upperUnderscoreToLowerHyphen(String text, String expected) {
        var result = Strings.upperUnderscoreToLowerHyphen(text);

        assertEquals(expected, result);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "BraveNewWorld, brave-new-world",
            "HHH, h-h-h",
            "H, h",
            "'', ''",
            "null, null"
    }, nullValues = "null")
    void upperCamelToLowerHyphen(String text, String expected) {
        var result = Strings.upperCamelToLowerHyphen(text);

        assertEquals(expected, result);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "braveNewWorld, BraveNewWorld",
            "h, H",
            "'', ''",
            "null, null"
    }, nullValues = "null")
    void lowerCamelToUpperCamel(String text, String expected) {
        var result = Strings.lowerCamelToUpperCamel(text);

        assertEquals(expected, result);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "braveNewWorld, brave-new-world",
            "h, h",
            "'', ''",
            "null, null"
    }, nullValues = "null")
    void lowerCamelToLowerHyphen(String text, String expected) {
        var result = Strings.lowerCamelToLowerHyphen(text);

        assertEquals(expected, result);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "brave_new_world, braveNewWorld",
            "h, h",
            "'', ''",
            "null, null"
    }, nullValues = "null")
    void lowerUnderscoreToLowerCamel(String text, String expected) {
        var result = Strings.lowerUnderscoreToLowerCamel(text);

        assertEquals(expected, result);
    }

    @Test
    void createAbbreviation() {
        var items = List.of("first", "second", "third");

        var abbreviation = Strings.createAbbreviation(items, 2, identity());

        assertEquals("first, second, ...", abbreviation);
    }

    @Test
    void firstNotBlank() {
        String first = null;
        var second = "   ";
        var third = "text";
        var fourth = "ignored";

        var text = Strings.firstNotBlank(first, second, third, fourth);

        assertEquals("text", text);
    }
}