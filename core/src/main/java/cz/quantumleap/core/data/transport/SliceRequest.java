package cz.quantumleap.core.data.transport;

import org.springframework.data.domain.Sort;

import java.util.Map;

public class SliceRequest {

    // TODO Make this just a pojo... extend to Slice, limits to mapper(?)
    public static final int CHUNK_SIZE = 15;
    public static final int MAX_ITEMS = 2000;

    private final Map<String, Object> filter;
    private final int offset;
    private final int size;
    private final Sort sort;
    private final Long tablePreferencesId;

    public SliceRequest(Map<String, Object> filter, int offset, int size, Sort sort, Long tablePreferencesId) {
        this.filter = filter;
        this.offset = offset;
        this.size = size;
        this.sort = sort;
        this.tablePreferencesId = tablePreferencesId;
    }

    public static SliceRequest filteredSorted(Map<String, Object> filter, Sort sort) {
        return new SliceRequest(
                filter,
                0,
                MAX_ITEMS,
                sort,
                null
        );
    }

    public SliceRequest sort(Sort sort) {
        return new SliceRequest(filter, offset, size, sort, tablePreferencesId);
    }

    public Map<String, Object> getFilter() {
        return filter;
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

    public SliceRequest extend() {
        if (offset + size < MAX_ITEMS) {
            int nextSize = Math.min(MAX_ITEMS - offset, offset + size + CHUNK_SIZE);
            return new SliceRequest(filter, offset, nextSize, sort, tablePreferencesId);
        }
        return null;
    }
}
