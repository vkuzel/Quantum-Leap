package cz.quantumleap.core.common;

import java.util.HashSet;
import java.util.Set;

public final class Sets {

    @SafeVarargs
    public static <T> Set<T> intersection(Set<T>... sets) {
        Set<T> product = new HashSet<>();
        for (Set<T> set : sets) {
            product.addAll(set);
        }
        return product;
    }
}