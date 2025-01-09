package cz.quantumleap.core.utils;

import java.util.regex.Pattern;

import static cz.quantumleap.core.utils.Strings.isBlank;
import static java.util.regex.Pattern.compile;

public class Numbers {

    public static final Pattern NUMBER_PATTERN = compile("^-?\\d+(\\.?\\d+)?|-?\\.?\\d+$");

    public static boolean isParsable(String text) {
        if (isBlank(text)) return false;
        return NUMBER_PATTERN.matcher(text).matches();
    }
}
