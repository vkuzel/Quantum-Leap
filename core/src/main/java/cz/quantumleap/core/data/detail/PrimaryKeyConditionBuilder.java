package cz.quantumleap.core.data.detail;

import org.apache.commons.lang3.Validate;
import org.jooq.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PrimaryKeyConditionBuilder {

    private final Table<? extends Record> table;

    public PrimaryKeyConditionBuilder(Table<? extends Record> table) {
        this.table = table;
    }

    public Optional<Condition> buildFromRecord(Record record) {
        List<Field<Object>> fields = getPrimaryKeyFields();
        Validate.isTrue(fields.size() > 0, "Table " + table + " does not have any primary key fields!");

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
        return getPrimaryKeyField().eq(id);
    }

    public Condition buildFromIds(Set<Object> ids) {
        return getPrimaryKeyField().in(ids);
    }

    public Field<Object> getPrimaryKeyField() {
        List<Field<Object>> fields = getPrimaryKeyFields();
        Validate.isTrue(fields.size() == 1, "Table " + table + " has " + fields.size() + " primary key fields, expected to be one!");
        return fields.get(0);
    }

    private List<Field<Object>> getPrimaryKeyFields() {
        UniqueKey<? extends Record> primaryKey = table.getPrimaryKey();
        Validate.notNull(primaryKey, "Table " + table.getName() + " does not have primary key!");
        return Stream.of(primaryKey.getFieldsArray()).map(this::castField).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private Field<Object> castField(TableField<? extends Record, ?> field) {
        return (Field<Object>) field;
    }
}
