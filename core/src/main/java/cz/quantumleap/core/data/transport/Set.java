package cz.quantumleap.core.data.transport;

// Set gets data from same database structures as Enum.
public class Set {

    // Typically enumId is based on a database column name in upper underscore format.
    private String enumId;
    private java.util.Set<Value> values;

    public Set() {
    }

    public Set(String enumId, java.util.Set<Value> values) {
        this.enumId = enumId;
        this.values = values;
    }

    public String getEnumId() {
        return enumId;
    }

    public void setEnumId(String enumId) {
        this.enumId = enumId;
    }

    public java.util.Set<Value> getValues() {
        return values;
    }

    public void setValues(java.util.Set<Value> values) {
        this.values = values;
    }

    public static class Value {

        private String id;
        private String label;

        public Value() {
        }

        public Value(String id, String label) {
            this.id = id;
            this.label = label;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }
}
