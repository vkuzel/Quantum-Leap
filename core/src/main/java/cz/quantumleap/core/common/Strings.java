package cz.quantumleap.core.common;

import static com.google.common.base.CaseFormat.*;

public final class Strings {

    public static String upperUnderscoreToLowerHyphen(String text) {
        return UPPER_UNDERSCORE.to(LOWER_HYPHEN, text);
    }

    public static String upperCamelToLowerHyphen(String text) {
        return UPPER_CAMEL.to(LOWER_HYPHEN, text);
    }

    public static String lowerCamelToUpperCamel(String text) {
        return LOWER_CAMEL.to(UPPER_CAMEL, text);
    }

    public static String lowerCamelToLowerHyphen(String text) {
        return UPPER_CAMEL.to(LOWER_HYPHEN, text);
    }

    public static String lowerUnderscoreToLowerCamel(String text) {
        return LOWER_UNDERSCORE.to(LOWER_CAMEL, text);
    }
}
