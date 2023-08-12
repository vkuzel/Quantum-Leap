package cz.quantumleap.core.common;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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
        String result = Strings.upperUnderscoreToLowerHyphen(text);

        assertEquals(expected, result);
    }

    @ParameterizedTest
    @CsvSource(value = {
//            "BraveNewWorld, brave-new-world",
            "HHH, h-h-h",
//            "H, h",
//            "'', ''",
//            "null, null"
    }, nullValues = "null")
    void upperCamelToLowerHyphen(String text, String expected) {
        String result = Strings.upperCamelToLowerHyphen(text);

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
        String result = Strings.lowerCamelToUpperCamel(text);

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
        String result = Strings.lowerCamelToLowerHyphen(text);

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
        String result = Strings.lowerUnderscoreToLowerCamel(text);

        assertEquals(expected, result);
    }
}