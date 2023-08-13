package cz.quantumleap.core.utils;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public final class Lists {

    @SafeVarargs
    public static <T> List<T> newArrayList(T... items) {
        return new ArrayList<>(asList(items));
    }
}
