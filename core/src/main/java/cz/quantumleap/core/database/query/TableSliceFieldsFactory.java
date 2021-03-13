package cz.quantumleap.core.database.query;

import cz.quantumleap.core.database.domain.SliceRequest;
import org.jooq.Field;

import java.util.List;

public interface TableSliceFieldsFactory {

    List<Field<?>> forSliceRequest(SliceRequest request);
}
