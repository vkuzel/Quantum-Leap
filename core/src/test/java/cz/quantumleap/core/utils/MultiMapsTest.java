package cz.quantumleap.core.utils;

import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MultiMapsTest {

    @Test
    void groupBy() {
        var collection = List.of("first", "second");

        var multimap = MultiMaps.groupBy(collection, s -> "key");

        assertEquals(collection, multimap.get("key"));
        assertEquals(emptyList(), multimap.get("missing"));
    }
}