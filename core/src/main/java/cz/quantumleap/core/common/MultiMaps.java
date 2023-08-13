package cz.quantumleap.core.common;

import java.util.*;
import java.util.function.Function;

import static java.util.Collections.emptyList;

public final class MultiMaps {

    /**
     * Preserves collection order.
     * Resulting map returns empty list for missing keys.
     */
    public static <K, V> Map<K, List<V>> groupBy(Collection<V> collection, Function<V, K> groupBy) {
        Map<K, List<V>> mapOfLists = new LinkedHashMap<>() {
            @Override
            public List<V> get(Object key) {
                var list = super.get(key);
                return list != null ? list : emptyList();
            }
        };
        for (var value : collection) {
            var key = groupBy.apply(value);
            var values = mapOfLists.computeIfAbsent(key, k -> new ArrayList<>());
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
            for (var v : vs) {
                values.add(valueMap.apply(v));
            }
        });
        return values;
    }
}
