package cz.quantumleap.core.data.transport;

import org.jooq.Name;
import org.jooq.Table;

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

    public static Lookup withoutLabel(Object id, Table table) {
        Name name = table.getQualifiedName();
        return new Lookup(id, null, name.toString());
    }

    public boolean isEmpty() {
        return id == null;
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
