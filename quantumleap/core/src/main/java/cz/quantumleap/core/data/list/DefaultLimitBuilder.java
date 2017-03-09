package cz.quantumleap.core.data.list;

import cz.quantumleap.core.data.transport.SliceRequest;

public class DefaultLimitBuilder implements LimitBuilder {

    @Override
    public Limit build(SliceRequest request) {
        return new Limit(request.getOffset(), Math.min(request.getSize() + 1, SliceRequest.MAX_ITEMS));
    }
}
