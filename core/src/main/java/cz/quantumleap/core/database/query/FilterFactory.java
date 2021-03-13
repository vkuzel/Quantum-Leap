package cz.quantumleap.core.database.query;

import cz.quantumleap.core.database.domain.SliceRequest;
import org.jooq.Condition;
import org.jooq.Field;

import java.util.List;

public interface FilterFactory {

    Condition forQuery(List<Field<?>> fields, String query);

    Condition forSliceRequest(List<Field<?>> fields, SliceRequest request);
}
