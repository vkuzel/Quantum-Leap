package cz.quantumleap.core.data.primarykey;

import org.apache.commons.lang3.Validate;
import org.jooq.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TablePrimaryKeyResolver implements PrimaryKeyResolver {

    private final Table<? extends Record> table;

    public TablePrimaryKeyResolver(Table<? extends Record> table) {
        this.table = table;
    }

    @Override
    public Field<Object> getPrimaryKeyField() {
        List<Field<Object>> fields = getPrimaryKeyFields();
        Validate.isTrue(fields.size() == 1, "Table %s has %d primary key fields, expected to be one!", table, fields.size());
        return fields.get(0);
    }

    @Override
    public List<Field<Object>> getPrimaryKeyFields() {
        UniqueKey<? extends Record> primaryKey = table.getPrimaryKey();
        if (primaryKey != null) {
            return Stream.of(primaryKey.getFieldsArray()).map(this::castField).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    private Field<Object> castField(TableField<? extends Record, ?> field) {
        return (Field<Object>) field;
    }
}
