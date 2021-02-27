package cz.quantumleap.core.database.domain;

public class IdLabel {

    private Object id;
    private String label;

    public IdLabel() {
    }

    public IdLabel(Object id, String label) {
        this.id = id;
        this.label = label;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
