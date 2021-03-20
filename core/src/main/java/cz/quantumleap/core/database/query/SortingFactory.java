package cz.quantumleap.core.database.query;

import cz.quantumleap.core.database.domain.SliceRequest;
import org.jooq.Field;
import org.jooq.SortField;

import java.util.List;
import java.util.Map;

public interface SortingFactory {

    List<SortField<?>> forSliceRequest(Map<String, Field<?>> fieldMap, SliceRequest request);

    List<SortField<?>> forLookup();
}
