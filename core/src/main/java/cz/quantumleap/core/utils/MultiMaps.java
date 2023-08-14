package cz.quantumleap.core.utils;

import java.util.*;
import java.util.function.Function;

import static java.util.Collections.emptyList;

public final class MultiMaps {

    /**
     * Preserves collection order.
     * Resulting map returns empty list for missing keys.
     */
    public static <K, V> Map<K, List<V>> groupBy(Collection<V> collection, Function<V, K> groupBy) {
        var multimap = new Multimap<K, V>();
        for (var value : collection) {
            var key = groupBy.apply(value);
            var values = multimap.computeIfAbsent(key, k -> new ArrayList<>());
            values.add(value);
        }
        return multimap;
    }

    public static <V> List<V> toValues(Map<?, List<V>> mapOfLists) {
        var values = new ArrayList<V>();
        mapOfLists.forEach((o, vs) -> values.addAll(vs));
        return values;
    }

    public static <V, T> List<T> mapValues(Map<?, List<V>> mapOfLists, Function<V, T> valueMap) {
        var values = new ArrayList<T>();
        mapOfLists.forEach((o, vs) -> {
            for (var v : vs) {
                var value = valueMap.apply(v);
                values.add(value);
            }
        });
        return values;
    }

    private static class Multimap<K, V> extends LinkedHashMap<K, List<V>> {

        public Multimap() {
        }

        @Override
        public List<V> get(Object key) {
            var list = super.get(key);
            return list != null ? list : emptyList();
        }
    }
}
