package cz.quantumleap.core.database.entity;

import org.jooq.Field;

public class EnumMetaType implements FieldMetaType {

    private final String enumId;

    public EnumMetaType(String enumId) {
        this.enumId = enumId;
    }

    public EnumMetaType(Field<?> field) {
        this.enumId = field.getName().toUpperCase();
    }

    public String getEnumId() {
        return enumId;
    }
}
