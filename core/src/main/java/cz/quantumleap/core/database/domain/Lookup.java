package cz.quantumleap.core.database.domain;

import cz.quantumleap.core.database.entity.EntityIdentifier;
import org.jooq.Record;
import org.jooq.Table;

// TODO Integrate into TableSlice
public class Lookup<TABLE extends Table<? extends Record>> {

    private Object id;
    private String label;
    private EntityIdentifier<TABLE> entityIdentifier;

    public Lookup() {
    }

    public Lookup(Object id, String label, EntityIdentifier<TABLE> entityIdentifier) {
        this.id = id;
        this.label = label;
        this.entityIdentifier = entityIdentifier;
    }

    public static <TABLE extends Table<? extends Record>> Lookup<TABLE> withoutLabel(Object id, TABLE table) {
        return new Lookup<>(id, null, EntityIdentifier.forTable(table));
    }

    public static <TABLE extends Table<? extends Record>> Lookup<TABLE> withoutLabel(Object id, EntityIdentifier<TABLE> entityIdentifier) {
        return new Lookup<>(id, null, entityIdentifier);
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

    public EntityIdentifier<TABLE> getEntityIdentifier() {
        return entityIdentifier;
    }

    public void setEntityIdentifier(EntityIdentifier<TABLE> entityIdentifier) {
        this.entityIdentifier = entityIdentifier;
    }

    @Override
    public String toString() {
        return "Lookup{" +
                "id=" + id +
                ", label='" + label + '\'' +
                ", entityIdentifier=" + entityIdentifier +
                '}';
    }
}
