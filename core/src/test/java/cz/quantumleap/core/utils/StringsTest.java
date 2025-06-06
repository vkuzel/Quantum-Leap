package cz.quantumleap.core.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.regex.Pattern;

import static java.util.function.Function.identity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @ParameterizedTest
    @CsvSource(value = {
            "null, true",
            "'', true",
            "'   ', true",
            "test, false"
    }, nullValues = "null")
    void isBlank(String text, boolean expected) {
        var result = Strings.isBlank(text);

        assertEquals(expected, result);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "null, false",
            "'', false",
            "'   ', false",
            "test, true",
    }, nullValues = "null")
    void isNotBlank(String text, boolean expected) {
        var result = Strings.isNotBlank(text);

        assertEquals(expected, result);
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

    @ParameterizedTest
    @CsvSource(value = {
            "null, null",
            "' a ', 'a'"
    }, nullValues = "null")
    void trim(String text, String expected) {
        var result = Strings.trim(text);

        assertEquals(expected, result);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "null, '', null",
            "'', '', ''",
            "'  XXX  ', null, 'XXX'",
            "'', 'abc', ''",
            "'aaabbbccc', 'abc', ''",
            "'aabbXXXabcYYYbbcc', 'abc', 'XXXabcYYY'",
    }, nullValues = "null")
    void trimWithChars(String text, String charsToTrim, String expected) {
        var result = Strings.trim(text, charsToTrim);

        assertEquals(expected, result);
    }

    @Test
    void stripAccents() {
        var text = "Příliš žluťoučký kůň úpěł ďábelské ódy.";

        var result = Strings.stripAccents(text);

        assertEquals("Prilis zlutoucky kun upel dabelske ody.", result);
    }

    @Test
    void randomAlphabetic() {
        var expectedCharsPattern = Pattern.compile("^[a-zA-Z]{64}$");

        var token = Strings.randomAlphabetic(64);

        assertTrue(
                expectedCharsPattern.matcher(token).matches(),
                () -> "Text '%s' does not match pattern '%s'".formatted(token, expectedCharsPattern)
        );
    }

    @ParameterizedTest
    @CsvSource(value = {
            "null, null",
            "'', ''",
            "abc, abc",
            "abcdefghi, abcdef..."
    }, nullValues = "null")
    void abbreviate(String text, String expected) {
        var result = Strings.abbreviate(text, 6);

        assertEquals(expected, result);
    }
}