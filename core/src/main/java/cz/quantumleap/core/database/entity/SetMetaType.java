package cz.quantumleap.core.database.entity;

import org.jooq.Field;

public class SetMetaType implements FieldMetaType {

    private final String enumId;

    public SetMetaType(String enumId) {
        this.enumId = enumId;
    }

    public SetMetaType(Field<?> field) {
        this.enumId = field.getName().toUpperCase();
    }

    public String getEnumId() {
        return enumId;
    }
}
