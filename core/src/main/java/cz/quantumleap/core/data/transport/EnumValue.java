package cz.quantumleap.core.data.transport;

public class EnumValue {

    // Typically enumId is based on a database column name in upper underscore format.
    private String enumId;
    private String id;
    private String label;

    public EnumValue() {
    }

    public EnumValue(String enumId, String id, String label) {
        this.enumId = enumId;
        this.id = id;
        this.label = label;
    }

    public static EnumValue fromEnum(Enum enumValue) {
        return new EnumValue(null, enumValue.name(), null);
    }

    public boolean isEmpty() {
        return id == null;
    }

    public String getEnumId() {
        return enumId;
    }

    public void setEnumId(String enumId) {
        this.enumId = enumId;
    }

    public String getId() {
        return id;
    }

    public <T extends Enum<T>> T asEnum(Class<T> type) {
        return T.valueOf(type, id);
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setId(java.lang.Enum id) {
        this.id = id.name();
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
