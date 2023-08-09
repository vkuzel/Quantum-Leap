package cz.quantumleap.core.common;

import java.util.*;
import java.util.function.Function;

public final class MultiMaps {

    /**
     * Preserves collection order.
     */
    public static <K, V> Map<K, List<V>> groupBy(Collection<V> collection, Function<V, K> groupBy) {
        Map<K, List<V>> mapOfLists = new LinkedHashMap<>();
        for (V value : collection) {
            K key = groupBy.apply(value);
            List<V> values = mapOfLists.computeIfAbsent(key, k -> new ArrayList<>());
            values.add(value);
        }
        return mapOfLists;
    }

    public static <V> List<V> toValues(Map<?, List<V>> mapOfLists) {
        List<V> values = new ArrayList<>();
        mapOfLists.forEach((o, vs) -> values.addAll(vs));
        return values;
    }

    public static <V, T> List<T> mapValues(Map<?, List<V>> mapOfLists, Function<V, T> valueMap) {
        List<T> values = new ArrayList<>();
        mapOfLists.forEach((o, vs) -> {
            for (V v : vs) {
                values.add(valueMap.apply(v));
            }
        });
        return values;
    }
}
