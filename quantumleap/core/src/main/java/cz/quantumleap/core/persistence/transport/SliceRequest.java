package cz.quantumleap.core.persistence.transport;

import org.springframework.data.domain.Sort;

public class SliceRequest {

    // TODO Make this just a pojo... extend to Slice, limits to mapper(?)
    public static final int CHUNK_SIZE = 15;
    public static final int MAX_ITEMS = 2000;

    private final int offset;
    private final int size;
    private final Sort sort;

    public SliceRequest(int offset, int size, Sort sort) {
        this.offset = offset;
        this.size = size;
        this.sort = sort;
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

    public SliceRequest extend() {
        if (offset + size < MAX_ITEMS) {
            int nextSize = Math.min(MAX_ITEMS - offset, offset + size + CHUNK_SIZE);
            return new SliceRequest(offset, nextSize, sort);
        }
        return null;
    }
}
