package cz.quantumleap.core.database.query;

import cz.quantumleap.core.database.EntityRegistry;
import cz.quantumleap.core.database.domain.SliceRequest;
import cz.quantumleap.core.database.entity.*;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.impl.DSL;

import java.util.ArrayList;
import java.util.List;

import static cz.quantumleap.core.database.query.QueryUtils.resolveLookupFieldName;
import static cz.quantumleap.core.database.query.QueryUtils.resolveTableAlias;
import static cz.quantumleap.core.tables.EnumValueTable.ENUM_VALUE;

public final class DefaultTableSliceFieldsFactory implements TableSliceFieldsFactory {

    private final Entity<?> entity;
    private final EntityRegistry entityRegistry;

    public DefaultTableSliceFieldsFactory(Entity<?> entity, EntityRegistry entityRegistry) {
        this.entity = entity;
        this.entityRegistry = entityRegistry;
    }

    @Override
    public List<Field<?>> forSliceRequest(SliceRequest request) {
        List<Field<?>> entityFields = entity.getFields();
        List<Field<?>> fields = new ArrayList<>(entityFields.size());

        for (Field<?> field : entityFields) {
            FieldMetaType fieldMetaType = entity.getFieldMetaType(field);
            if (fieldMetaType instanceof EnumMetaType) {
                Table<?> enumTable = resolveTableAlias(ENUM_VALUE, field);

                Field<?> enumField = getFieldSafely(enumTable, ENUM_VALUE.LABEL).as(field);

                fields.add(enumField);
            } else if (fieldMetaType instanceof SetMetaType) {
                String enumId = fieldMetaType.asSet().getEnumId();

                Field<?> setField = DSL.coalesce(
                        DSL
                                .select(DSL.groupConcat(ENUM_VALUE.LABEL).separator(", "))
                                .from(ENUM_VALUE)
                                .where(ENUM_VALUE.ENUM_ID.eq(enumId)
                                        .and(ENUM_VALUE.ID.eq(DSL.any(toArrayField(field))))),
                        DSL.val("")
                ).as(field);

                fields.add(setField);
            } else if (fieldMetaType instanceof LookupMetaType) {
                EntityIdentifier<?> lookupEntityIdentifier = fieldMetaType.asLookup().getEntityIdentifier();
                Entity<?> lookupEntity = entityRegistry.getEntity(lookupEntityIdentifier);
                Table<?> lookupTable = resolveTableAlias(lookupEntity.getTable(), field);

                String labelFieldName = resolveLookupFieldName(field);
                Field<?> labelField = lookupEntity.buildLookupLabelFieldForTable(lookupTable).as(labelFieldName);

                fields.add(field);
                fields.add(labelField);
            } else {
                fields.add(field);
            }
        }

        return fields;
    }

    @SuppressWarnings("unchecked")
    private Field<String[]> toArrayField(Field<?> field) {
        return (Field<String[]>) field;
    }

    private Field<?> getFieldSafely(Table<?> table, Field<?> field) {
        Field<?> retrieved = table.field(field);
        if (retrieved == null) {
            throw new IllegalArgumentException("Field " + field + " not found in table " + table);
        }
        return retrieved;
    }
}
