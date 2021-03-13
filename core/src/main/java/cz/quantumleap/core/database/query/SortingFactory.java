package cz.quantumleap.core.database.query;

import cz.quantumleap.core.database.domain.SliceRequest;
import org.jooq.Field;
import org.jooq.SortField;

import java.util.List;

public interface SortingFactory {

    List<SortField<?>> forSliceRequest(List<Field<?>> fields, SliceRequest request);

    List<SortField<?>> forLookup();
}
