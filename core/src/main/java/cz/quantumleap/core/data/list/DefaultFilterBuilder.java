package cz.quantumleap.core.data.list;

import org.apache.commons.lang3.Validate;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;

import java.util.*;

public class DefaultFilterBuilder implements FilterBuilder {

    private final Table<? extends Record> table;

    public DefaultFilterBuilder(Table<? extends Record> table) {
        this.table = table;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Condition> build(Map<String, Object> filter) {
        if (filter.isEmpty()) {
            return Collections.emptyList();
        }

        List<Condition> conditions = new ArrayList<>(filter.size());
        filter.forEach((fieldName, value) -> {
            Field<Object> field = (Field<Object>) table.field(fieldName);
            Validate.notNull(field, "Field %s not found in table %s", fieldName, table.getName());
            conditions.add(field.eq(value));
        });
        return conditions;
    }
}
