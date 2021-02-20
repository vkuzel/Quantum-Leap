package cz.quantumleap.core.data.query;

import cz.quantumleap.core.data.EntityManager;
import cz.quantumleap.core.data.entity.Entity;
import cz.quantumleap.core.data.list.LimitBuilder.Limit;
import cz.quantumleap.core.data.transport.SliceRequest;

public class TableSliceLimitFactory {

    private final Entity<?> entity;
    private final EntityManager entityManager;

    public TableSliceLimitFactory(Entity<?> entity, EntityManager entityManager) {
        this.entity = entity;
        this.entityManager = entityManager;
    }

    public Limit forSliceRequest(SliceRequest request) {
        return entity.getLimitBuilder().build(request);
    }
}
