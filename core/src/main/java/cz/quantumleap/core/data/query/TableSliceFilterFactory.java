package cz.quantumleap.core.data.query;

import cz.quantumleap.core.common.Utils;
import cz.quantumleap.core.data.EntityManager;
import cz.quantumleap.core.data.entity.Entity;
import cz.quantumleap.core.data.transport.SliceRequest;
import org.jooq.Condition;
import org.jooq.Field;

import java.util.List;

public class TableSliceFilterFactory {

    private final Entity<?> entity;
    private final EntityManager entityManager;

    public TableSliceFilterFactory(Entity<?> entity, EntityManager entityManager) {
        this.entity = entity;
        this.entityManager = entityManager;
    }

    public Condition forSliceRequest(List<Field<?>> fields, SliceRequest request) {
        return Utils.joinConditions(
                Utils.ConditionOperator.AND,
                entity.getFilterBuilder().buildForFilter(request.getFilter()),
                entity.getFilterBuilder().buildForQuery(request.getQuery()),
                request.getCondition()
        );
    }
}
