package cz.quantumleap.core.data.transport;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

// Set gets data from same database structures as Enum.
public class SetValues {

    // Typically enumId is based on a database column name in upper underscore format.
    private String enumId;
    private ValueSet values;

    public SetValues() {
    }

    public SetValues(String enumId, ValueSet values) {
        this.enumId = enumId;
        this.values = values;
    }

    public boolean containsValueId(String id) {
        for (Value value : values) {
            if (Objects.equals(value.getId(), id)) {
                return true;
            }
        }
        return false;
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

    public void setValues(ValueSet values) {
        this.values = values;
    }

    // Generics type parameter is not available during runtime. Due to that,
    // the Jackson converter used in the JooqConverterProvider class converts
    // set's values into a map instead of value object. This class provides
    // correct type to the converter.
    public static class ValueSet extends HashSet<Value> {

        public ValueSet() {
        }

        public ValueSet(int initialCapacity) {
            super(initialCapacity);
        }
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
