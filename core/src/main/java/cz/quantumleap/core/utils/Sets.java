package cz.quantumleap.core.utils;

import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.emptySet;

public final class Sets {

    @SafeVarargs
    public static <T> Set<T> intersection(Set<T>... sets) {
        if (sets.length == 0) {
            return emptySet();
        }

        var product = new HashSet<T>();
        for (var value : sets[0]) {
            var allContains = true;
            for (var i = 1; i < sets.length; i++) {
                var otherSet = sets[i];
                if (!otherSet.contains(value)) {
                    allContains = false;
                    break;
                }
            }
            if  (allContains) {
                product.add(value);
            }
        }
        return product;
    }
}
