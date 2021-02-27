package cz.quantumleap.core.database.query;

import cz.quantumleap.core.database.domain.SliceRequest;

public final class LimitFactory {

    public Limit forSliceRequest(SliceRequest request) {
        return new Limit(request.getOffset(), Math.min(request.getSize() + 1, SliceRequest.MAX_ITEMS));
    }

    public static final class Limit {
        private final int offset;
        private final int numberOfRows;

        public Limit(int offset, int numberOfRows) {
            this.offset = offset;
            this.numberOfRows = numberOfRows;
        }

        public int getOffset() {
            return offset;
        }

        public int getNumberOfRows() {
            return numberOfRows;
        }
    }
}
