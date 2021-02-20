package cz.quantumleap.core.data.query;

import cz.quantumleap.core.data.EntityManager;
import cz.quantumleap.core.data.entity.Entity;
import cz.quantumleap.core.data.transport.SliceRequest;
import org.jooq.Field;
import org.jooq.SortField;

import java.util.List;

public class TableSliceOrderByFactory {

    private final Entity<?> entity;
    private final EntityManager entityManager;

    public TableSliceOrderByFactory(Entity<?> entity, EntityManager entityManager) {
        this.entity = entity;
        this.entityManager = entityManager;
    }

    public List<SortField<?>> forSliceRequest(List<Field<?>> fields, SliceRequest request) {
        return entity.getSortingBuilder().build(request.getSort());
    }
}
