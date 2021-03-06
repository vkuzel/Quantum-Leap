package cz.quantumleap.core.common;

import com.google.common.io.CharStreams;
import cz.quantumleap.core.database.entity.EntityIdentifier;
import org.apache.commons.lang3.Validate;
import org.jooq.Record;
import org.jooq.Table;
import org.springframework.core.io.Resource;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.function.Function;

final public class Utils {

    private static final String AJAX_HEADER_NAME = "X-Requested-With";
    private static final String AJAX_HEADER_VALUE = "XMLHttpRequest";

    public static String readResourceToString(Resource resource) {
        try (InputStreamReader reader = new InputStreamReader(resource.getInputStream())) {
            return CharStreams.toString(reader);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static boolean isAjaxRequest(HttpServletRequest request) {
        return AJAX_HEADER_VALUE.equals(request.getHeader(AJAX_HEADER_NAME));
    }

    /**
     * Null is more than any other value.
     */
    @SafeVarargs
    public static <T extends Comparable<? super T>> T min(T... items) {
        T min = null;
        for (T item : items) {
            if (item != null) {
                if (min == null) {
                    min = item;
                } else if (item.compareTo(min) < 0) {
                    min = item;
                }
            }
        }
        return min;
    }

    /**
     * Null is less than any other value.
     */
    @SafeVarargs
    public static <T extends Comparable<? super T>> T max(T... items) {
        T max = null;
        for (T item : items) {
            if (item != null) {
                if (max == null) {
                    max = item;
                } else if (item.compareTo(max) > 0) {
                    max = item;
                }
            }
        }
        return max;
    }

    public static <T> String createAbbreviation(Collection<T> items, int maxSize, Function<T, String> mapToText) {
        Set<String> textItems = new LinkedHashSet<>(maxSize);
        for (T item : items) {
            textItems.add(mapToText.apply(item));
            if (textItems.size() >= maxSize - 1) {
                textItems.add("...");
                break;
            }
        }
        return String.join(", ", textItems);
    }

    public static List<LocalDate> generateDaysBetween(LocalDate start, LocalDate end) {
        if (end == null) {
            end = start;
        }

        int daysCount = Period.between(start, end).getDays();
        Validate.isTrue(daysCount < 1000);

        LocalDate date = start;
        List<LocalDate> daysBetween = new ArrayList<>(daysCount);
        while (date.isBefore(end) || date.isEqual(end)) {
            daysBetween.add(date);
            date = date.plusDays(1);
        }
        return daysBetween;
    }

    @SuppressWarnings("unchecked")
    public static <TABLE extends Table<? extends Record>> EntityIdentifier<TABLE> checkTableType(
            EntityIdentifier<?> entityIdentifier, Class<TABLE> tableType
    ) {
        Table<? extends Record> table = entityIdentifier.getTable();
        if (tableType == null || tableType.isInstance(table)) {
            return (EntityIdentifier<TABLE>) entityIdentifier;
        }

        String msg = "Entity identifier " + entityIdentifier + " is not compatible with " + tableType;
        throw new IllegalStateException(msg);
    }
}
