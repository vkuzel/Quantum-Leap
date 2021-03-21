package cz.quantumleap.core.database.domain;

import org.apache.commons.lang3.Validate;
import org.jooq.Condition;
import org.springframework.data.domain.Sort;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;

public class FetchParams {

    public static final int CHUNK_SIZE = 15;
    public static final int MAX_ITEMS = 2000;

    private final Map<String, Object> filter;
    private final String query;
    private final Condition condition;

    private final int offset;
    private final int size;

    private final Sort sort;

    private final Long tablePreferencesId;

    public FetchParams(Map<String, Object> filter, String query, Condition condition, int offset, int size, Sort sort, Long tablePreferencesId) {
        Validate.notNull(sort);
        this.filter = unmodifiableMap(filter);
        this.query = query;
        this.condition = condition;
        this.offset = offset;
        this.size = size;
        this.sort = sort;
        this.tablePreferencesId = tablePreferencesId;
    }

    public static FetchParams filteredSorted(Map<String, Object> filter, Sort sort) {
        return new FetchParams(
                filter,
                null,
                null,
                0,
                MAX_ITEMS,
                sort,
                null
        );
    }

    public FetchParams addCondition(Condition condition) {
        Condition newCondition = this.condition != null ? this.condition.and(condition) : condition;
        return new FetchParams(filter, query, newCondition, offset, size, sort, tablePreferencesId);
    }

    public FetchParams withSort(Sort sort) {
        return new FetchParams(filter, query, condition, offset, size, sort, tablePreferencesId);
    }

    public FetchParams addFilter(String fieldName, Object value) {
        Map<String, Object> filter = new HashMap<>(this.filter);
        filter.put(fieldName, value);
        return new FetchParams(filter, query, condition, offset, size, sort, tablePreferencesId);
    }

    public Map<String, Object> getFilter() {
        return filter;
    }

    public String getQuery() {
        return query;
    }

    public Condition getCondition() {
        return condition;
    }

    public int getOffset() {
        return offset;
    }

    public int getSize() {
        return size;
    }

    public Sort getSort() {
        return sort;
    }

    public Long getTablePreferencesId() {
        return tablePreferencesId;
    }

    public FetchParams extend() {
        if (offset + size < MAX_ITEMS) {
            int nextSize = Math.min(MAX_ITEMS - offset, offset + size + CHUNK_SIZE);
            return new FetchParams(filter, query, condition, offset, nextSize, sort, tablePreferencesId);
        }
        return null;
    }
}
