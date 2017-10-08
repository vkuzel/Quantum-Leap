package cz.quantumleap.core.data.transport;

public class Lookup {

    private Object id;
    private String label;
    private String databaseTableNameWithSchema;

    public Lookup() {
    }

    public Lookup(Object id, String label, String databaseTableNameWithSchema) {
        this.id = id;
        this.label = label;
        this.databaseTableNameWithSchema = databaseTableNameWithSchema;
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

    public String getDatabaseTableNameWithSchema() {
        return databaseTableNameWithSchema;
    }

    public void setDatabaseTableNameWithSchema(String databaseTableNameWithSchema) {
        this.databaseTableNameWithSchema = databaseTableNameWithSchema;
    }
}
