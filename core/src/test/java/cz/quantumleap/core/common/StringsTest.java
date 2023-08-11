package cz.quantumleap.core.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringsTest {

    @Test
    void upperUnderscoreToLowerHyphen() {
        String text = "HELLO_WORLD";

        String result = Strings.upperUnderscoreToLowerHyphen(text);

        assertEquals("hello-world", result);
    }

    @Test
    void upperCamelToLowerHyphen() {
        String text = "HelloWorld";

        String result = Strings.upperCamelToLowerHyphen(text);

        assertEquals("hello-world", result);
    }

    @Test
    void lowerCamelToUpperCamel() {
        String text = "helloWorld";

        String result = Strings.lowerCamelToUpperCamel(text);

        assertEquals("HelloWorld", result);
    }

    @Test
    void lowerCamelToLowerHyphen() {
        String text = "helloWorld";

        String result = Strings.lowerCamelToLowerHyphen(text);

        assertEquals("hello-world", result);
    }

    @Test
    void lowerUnderscoreToLowerCamel() {
        String text = "hello_world";

        String result = Strings.lowerUnderscoreToLowerCamel(text);

        assertEquals("helloWorld", result);
    }
}