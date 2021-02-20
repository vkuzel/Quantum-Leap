package cz.quantumleap.core.data.entity;

public interface FieldMetaType {

    default EnumMetaType asEnum() {
        return (EnumMetaType) this;
    }

    default SetMetaType asSet() {
        return (SetMetaType) this;
    }

    default LookupMetaType asLookup() {
        return (LookupMetaType) this;
    }

}
