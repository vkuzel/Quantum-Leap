package cz.quantumleap.core.data.primarykey;

import org.apache.commons.lang3.Validate;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class PrimaryKeyConditionBuilder {

    private final PrimaryKeyResolver primaryKeyResolver;

    public PrimaryKeyConditionBuilder(PrimaryKeyResolver primaryKeyResolver) {
        this.primaryKeyResolver = primaryKeyResolver;
    }

    public Optional<Condition> buildFromRecord(Record record) {
        List<Field<Object>> fields = primaryKeyResolver.getPrimaryKeyFields();
        Validate.isTrue(fields.size() > 0, "No primary key fields has been found!");

        Condition condition = null;

        for (Field<Object> field : fields) {
            Object value = record.getValue(field);
            if (value == null) {
                return Optional.empty();
            }

            if (condition == null) {
                condition = field.eq(value);
            } else {
                condition = condition.and(field.eq(value));
            }
        }

        return Optional.of(condition);
    }

    public Condition buildFromId(Object id) {
        return primaryKeyResolver.getPrimaryKeyField().eq(id);
    }

    public Condition buildFromIds(Set<Object> ids) {
        return primaryKeyResolver.getPrimaryKeyField().in(ids);
    }

    public Field<Object> getPrimaryKeyField() {
        return primaryKeyResolver.getPrimaryKeyField();
    }
}
