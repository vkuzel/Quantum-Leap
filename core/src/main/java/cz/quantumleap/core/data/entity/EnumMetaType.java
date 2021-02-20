package cz.quantumleap.core.data.entity;

import org.jooq.Field;

public class EnumMetaType implements FieldMetaType {

    private final String enumId;

    public EnumMetaType(String enumId) {
        this.enumId = enumId;
    }

    public EnumMetaType(Field<?> field) {
        // TODO Format name!
        this.enumId = field.getName();
    }

    public String getEnumId() {
        return enumId;
    }
}
