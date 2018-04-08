package cz.quantumleap.core.data.list;

import org.jooq.Condition;

import java.util.Map;

public interface FilterBuilder {

    Condition buildForFilter(Map<String, Object> filter);

    Condition buildForQuery(String query);
}
