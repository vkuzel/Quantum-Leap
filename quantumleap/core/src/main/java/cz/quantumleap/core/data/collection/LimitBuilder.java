package cz.quantumleap.core.data.collection;

import cz.quantumleap.core.data.transport.SliceRequest;

public interface LimitBuilder {

    LimitBuilder DEFAULT = new DefaultLimitBuilder();

    Limit build(SliceRequest request);

    class Limit {
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