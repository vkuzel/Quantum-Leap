package cz.quantumleap.core.database.entity;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;

import java.util.Set;

public class PrimaryKeyConditionBuilder<TABLE extends Table<? extends Record>> {

    private final Entity<TABLE> entity;

    PrimaryKeyConditionBuilder(Entity<TABLE> entity) {
        this.entity = entity;
    }

    public Condition buildFromRecord(Record record) {
        Condition condition = null;
        for (var field : entity.getPrimaryKeyFields()) {
            var value = record.getValue(field);
            if (value == null) {
                return null;
            }

            var casted = castField(field);
            if (condition == null) {
                condition = casted.eq(value);
            } else {
                condition = condition.and(casted.eq(value));
            }
        }
        return condition;
    }

    public Condition buildFromId(Object id) {
        return getPrimaryKeyField().eq(id);
    }

    public Condition buildFromIds(Set<Object> ids) {
        return getPrimaryKeyField().in(ids);
    }

    private Field<Object> getPrimaryKeyField() {
        return castField(entity.getPrimaryKeyField());
    }

    @SuppressWarnings("unchecked")
    private Field<Object> castField(Field<?> field) {
        return (Field<Object>) field;
    }
}
