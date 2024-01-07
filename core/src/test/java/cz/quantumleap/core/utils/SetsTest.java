package cz.quantumleap.core.utils;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SetsTest {

    @Test
    void intersectionReturnsOnlySharedValues() {
        var set1 = Set.of(1, 10, 100);
        var set2 = Set.of(2, 10, 100);
        var set3 = Set.of(3, 10, 300);

        var intersection = Sets.intersection(set1, set2, set3);

        assertEquals(Set.of(10), intersection);
    }
}