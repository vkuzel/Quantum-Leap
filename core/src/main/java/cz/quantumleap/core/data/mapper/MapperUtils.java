package cz.quantumleap.core.data.mapper;

import org.jooq.Field;
import org.jooq.Table;

public class MapperUtils { // TODO Dro this method...

    public static String resolveDatabaseTableNameWithSchema(Table<?> table) {
        String name = table.getName();
        if (table.getSchema() != null) {
            name = table.getSchema().getName() + "." + name;
        }
        return name;
    }

    public static String resolveEnumId(Field<?> field) {
        return field.getName().toUpperCase();
    }
}
