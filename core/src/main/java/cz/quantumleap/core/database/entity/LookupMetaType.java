package cz.quantumleap.core.database.entity;

public class LookupMetaType implements FieldMetaType {

    private final EntityIdentifier<?> entityIdentifier;

    public LookupMetaType(EntityIdentifier<?> entityIdentifier) {
        this.entityIdentifier = entityIdentifier;
    }

    public EntityIdentifier<?> getEntityIdentifier() {
        return entityIdentifier;
    }
}
