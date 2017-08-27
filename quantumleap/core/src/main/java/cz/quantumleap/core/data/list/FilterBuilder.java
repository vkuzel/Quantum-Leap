package cz.quantumleap.core.data.list;

import org.jooq.Condition;

import java.util.Collection;
import java.util.Map;

public interface FilterBuilder {

    Collection<Condition> build(Map<String, Object> filter);

}
