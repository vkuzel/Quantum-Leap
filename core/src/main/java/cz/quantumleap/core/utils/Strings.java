package cz.quantumleap.core.utils;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;

public final class Strings {

    private static final Pattern UPPER_CAMEL_PATTERN = Pattern.compile("([A-Z])");
    private static final Pattern LOWER_CAMEL_PATTERN = Pattern.compile("([A-Z])");
    private static final Pattern LOWER_UNDERSCORE_PATTERN = Pattern.compile("_([a-z])");

    public static String upperUnderscoreToLowerHyphen(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.toLowerCase().replace('_', '-');
    }

    public static String upperCamelToLowerHyphen(String text) {
        if (text == null || text.isEmpty()) return text;
        var matcher = UPPER_CAMEL_PATTERN.matcher(text);
        return matcher.replaceAll(matchResult -> (matchResult.start() > 0 ? "-" : "") + matchResult.group(1)).toLowerCase();
    }

    public static String lowerCamelToUpperCamel(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    public static String lowerCamelToLowerHyphen(String text) {
        if (text == null || text.isEmpty()) return text;
        var matcher = LOWER_CAMEL_PATTERN.matcher(text);
        return matcher.replaceAll(matchResult -> '-' + matchResult.group(1).toLowerCase());
    }

    public static String lowerUnderscoreToLowerCamel(String text) {
        if (text == null || text.isEmpty()) return text;
        var matcher = LOWER_UNDERSCORE_PATTERN.matcher(text.toLowerCase());
        return matcher.replaceAll(matchResult -> matchResult.group(1).toUpperCase());
    }

    public static <T> String createAbbreviation(Collection<T> items, int maxSize, Function<T, String> mapToText) {
        Set<String> textItems = new LinkedHashSet<>(maxSize);
        for (var item : items) {
            textItems.add(mapToText.apply(item));
            if (textItems.size() >= maxSize) {
                textItems.add("...");
                break;
            }
        }
        return String.join(", ", textItems);
    }

    public static boolean isBlank(String text) {
        return text == null || text.isBlank();
    }

    public static boolean isNotBlank(String text) {
        return !isBlank(text);
    }

    public static String firstNotBlank(String... texts) {
        for (var text : texts) {
            if (text != null && !text.isBlank()) {
                return text;
            }
        }
        return null;
    }

    public static String trim(String text) {
        return trim(text, null);
    }

    public static String trim(String text, String trimChars) {
        if (text == null) {
            return null;
        } else if (trimChars == null) {
            return text.trim();
        } else if (trimChars.isEmpty()) {
            return text;
        } else {
            var length = text.length();
            var start = 0;
            while (start < length && trimChars.indexOf(text.charAt(start)) != -1) {
                start++;
            }
            var end = length;
            while (end > start && trimChars.indexOf(text.charAt(end - 1)) != -1) {
                end--;
            }
            return text.substring(start, end);
        }
    }
}
