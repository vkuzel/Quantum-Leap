package cz.quantumleap.core.utils;

import java.text.Normalizer.Form;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.random.RandomGenerator;
import java.util.regex.Pattern;

import static java.text.Normalizer.normalize;

public final class Strings {

    private static final Pattern UPPER_CAMEL_PATTERN = Pattern.compile("([A-Z])");
    private static final Pattern LOWER_CAMEL_PATTERN = Pattern.compile("([A-Z])");
    private static final Pattern LOWER_UNDERSCORE_PATTERN = Pattern.compile("_([a-z])");
    private static final Pattern STRIP_ACCENTS_PATTERN = Pattern.compile("\\p{InCombiningDiacriticalMarks}+"); //$NON-NLS-1$

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

    // Copied from the Apache commons library
    public static String stripAccents(String text) {
        if (text == null || text.isEmpty()) return text;
        var decomposed = new StringBuilder(normalize(text, Form.NFD));
        // Convert remaining characters
        for (var i = 0; i < decomposed.length(); i++) {
            switch (decomposed.charAt(i)) {
                case '\u0141' -> decomposed.setCharAt(i, 'L');
                case '\u0142' -> decomposed.setCharAt(i, 'l');
            }
        }
        // Note that this doesn't correctly remove ligatures...
        return STRIP_ACCENTS_PATTERN.matcher(decomposed).replaceAll("");
    }

    public static String randomAlphabetic(int length) {
        if (length <= 0) throw new IllegalArgumentException("Length must be positive, it is: " + length);
        var randomGenerator = RandomGenerator.getDefault();
        var builder = new StringBuilder(length);
        for (var i = 0; i < length; i++) {
            var num = randomGenerator.nextInt(0, 52);
            var ch = num < 26 ? num + 65 : num - 26 + 97;
            builder.append((char) ch);
        }
        return builder.toString();
    }

    public static String abbreviate(String text, int maxLength) {
        if (maxLength < 4) throw new IllegalArgumentException("Max length must be more than 4, it is: " + maxLength);
        if (text == null || text.length() < maxLength) return text;
        return text.substring(0, maxLength) + "...";
    }
}
