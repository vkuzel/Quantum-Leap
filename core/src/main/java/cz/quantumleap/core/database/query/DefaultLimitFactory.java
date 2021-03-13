package cz.quantumleap.core.database.query;

import cz.quantumleap.core.database.domain.SliceRequest;

public final class DefaultLimitFactory implements LimitFactory {

    @Override
    public Limit forSliceRequest(SliceRequest request) {
        return new Limit(request.getOffset(), Math.min(request.getSize() + 1, SliceRequest.MAX_ITEMS));
    }
}
