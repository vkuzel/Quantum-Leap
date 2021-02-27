package cz.quantumleap.core.data.query;

import org.jooq.Field;
import org.jooq.Table;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class QueryUtils {

    public static Table<?> resolveTableAlias(Table<?> table, Field<?> field) {
        String alias = "t_" + field.getName();
        return table.as(alias);
    }

    public static String resolveLookupIdFieldName(Field<?> field) {
        return field.getName() + ".id";
    }

    public static Map<String, Field<?>> createFieldMap(List<Field<?>> fields) {
        Map<String, Field<?>> fieldMap = new HashMap<>(fields.size());
        for (Field<?> field : fields) {
            String name = normalizeFieldName(field.getName());
            fieldMap.put(name, field);
        }
        return fieldMap;
    }

    public static String normalizeFieldName(String name) {
        return name.toLowerCase();
    }
}
