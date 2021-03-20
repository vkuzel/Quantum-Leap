package cz.quantumleap.core.database.query;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectJoinStep;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class QueryFields {

    private final List<Function<SelectJoinStep<Record>, SelectJoinStep<Record>>> joinTables;
    private final Map<String, Field<?>> queryFieldMap;
    private final Map<String, Field<?>> filterFieldMap;
    private final Map<String, Field<?>> orderFieldMap;

    public QueryFields(
            List<Function<SelectJoinStep<Record>, SelectJoinStep<Record>>> joinTables,
            Map<String, Field<?>> queryFieldMap,
            Map<String, Field<?>> filterFieldMap,
            Map<String, Field<?>> orderFieldMap
    ) {
        this.joinTables = joinTables;
        this.queryFieldMap = queryFieldMap;
        this.filterFieldMap = filterFieldMap;
        this.orderFieldMap = orderFieldMap;
    }

    public List<Function<SelectJoinStep<Record>, SelectJoinStep<Record>>> getJoinTables() {
        return joinTables;
    }

    public Map<String, Field<?>> getQueryFieldMap() {
        return queryFieldMap;
    }

    public Map<String, Field<?>> getFilterFieldMap() {
        return filterFieldMap;
    }

    public Map<String, Field<?>> getOrderFieldMap() {
        return orderFieldMap;
    }
}
