package cz.quantumleap.core.data.entity;

import org.jooq.Field;

public class SetMetaType implements FieldMetaType {

    private final String enumId;

    public SetMetaType(String enumId) {
        this.enumId = enumId;
    }

    public SetMetaType(Field<?> field) {
        // TODO Format!
        this.enumId = field.getName();
    }

    public String getEnumId() {
        return enumId;
    }
}
