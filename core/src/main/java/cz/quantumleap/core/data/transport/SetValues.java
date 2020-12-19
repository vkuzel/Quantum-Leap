package cz.quantumleap.core.data.transport;

import java.util.Set;

// Set gets data from same database structures as Enum.
public class SetValues {

    // Typically enumId is based on a database column name in upper underscore format.
    private String enumId;
    private Set<Value> values;

    public SetValues() {
    }

    public SetValues(String enumId, Set<Value> values) {
        this.enumId = enumId;
        this.values = values;
    }

    public String getEnumId() {
        return enumId;
    }

    public void setEnumId(String enumId) {
        this.enumId = enumId;
    }

    public Set<Value> getValues() {
        return values;
    }

    public void setValues(Set<Value> values) {
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
