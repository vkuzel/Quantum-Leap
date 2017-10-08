package cz.quantumleap.core.data.mapper;

import org.jooq.Table;

public class MapperUtils {

    public static String resolveDatabaseTableNameWithSchema(Table<?> table) {
        String name = table.getName();
        if (table.getSchema() != null) {
            name = table.getSchema().getName() + "." + name;
        }
        return name;
    }
}
