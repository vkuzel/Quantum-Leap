package cz.quantumleap.core.common;

import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MultiMapsTest {

    @Test
    void groupBy() {
        var collection = List.of("first", "second");

        var multiMap = MultiMaps.groupBy(collection, s -> "key");

        assertEquals(collection, multiMap.get("key"));
        assertEquals(emptyList(), multiMap.get("missing"));
    }
}