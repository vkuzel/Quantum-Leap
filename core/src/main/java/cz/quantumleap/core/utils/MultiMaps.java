package cz.quantumleap.core.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;

import static java.util.Collections.emptyList;

public final class MultiMaps {

    /**
     * Preserves collection order.
     * Resulting map returns empty list for missing keys.
     */
    public static <K, V> Multimap<K, V> groupBy(Collection<V> collection, Function<V, K> groupBy) {
        var multimap = new Multimap<K, V>();
        for (var value : collection) {
            var key = groupBy.apply(value);
            var values = multimap.computeIfAbsent(key, k -> new ArrayList<>());
            values.add(value);
        }
        return multimap;
    }

    public static class Multimap<K, V> extends LinkedHashMap<K, List<V>> {

        public Multimap() {
        }

        @Override
        public List<V> get(Object key) {
            var list = super.get(key);
            return list != null ? list : emptyList();
        }

        public List<V> allValues() {
            var values = new ArrayList<V>();
            forEach((o, vs) -> values.addAll(vs));
            return values;
        }

        public <T> List<T> mapAllValues(Function<V, T> mapper) {
            var values = new ArrayList<T>();
            forEach((o, vs) -> {
                for (var v : vs) {
                    var value = mapper.apply(v);
                    values.add(value);
                }
            });
            return values;
        }
    }
}
