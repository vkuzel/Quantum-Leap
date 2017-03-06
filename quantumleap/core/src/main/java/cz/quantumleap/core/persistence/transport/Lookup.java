package cz.quantumleap.core.persistence.transport;

public class Lookup {

    private Object id;
    private String label;
    private String tableName;

    public Lookup() {
    }

    public Lookup(Object id, String label, String tableName) {
        this.id = id;
        this.label = label;
        this.tableName = tableName;
    }

    public Object getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
