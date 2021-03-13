package cz.quantumleap.core.database.query;

import cz.quantumleap.core.database.domain.SliceRequest;

public interface LimitFactory {

    Limit forSliceRequest(SliceRequest request);

    final class Limit {
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
