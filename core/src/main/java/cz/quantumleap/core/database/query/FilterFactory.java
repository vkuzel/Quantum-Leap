package cz.quantumleap.core.database.query;

import cz.quantumleap.core.database.domain.SliceRequest;
import org.jooq.Condition;
import org.jooq.Field;

import java.util.Map;

public interface FilterFactory {

    Condition forQuery(Map<String, Field<?>> fieldMap, String query);

    Condition forSliceRequest(Map<String, Field<?>> fieldMap, SliceRequest request);
}
