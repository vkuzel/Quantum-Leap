package cz.quantumleap.core.data.primarykey;

import org.apache.commons.lang3.Validate;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.TableField;

import java.util.Collections;
import java.util.List;

public class TableFieldPrimaryKeyResolver implements PrimaryKeyResolver {

    private final Field<Object> primaryKeyField;

    @SuppressWarnings("unchecked")
    public TableFieldPrimaryKeyResolver(TableField<? extends Record, ?> field) {
        Validate.notNull(field);
        this.primaryKeyField = (Field<Object>) field;
    }

    @Override
    public Field<Object> getPrimaryKeyField() {
        return primaryKeyField;
    }

    @Override
    public List<Field<Object>> getPrimaryKeyFields() {
        return Collections.singletonList(primaryKeyField);
    }
}
