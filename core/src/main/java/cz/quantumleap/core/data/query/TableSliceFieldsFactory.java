package cz.quantumleap.core.data.query;

import cz.quantumleap.core.data.EntityManager;
import cz.quantumleap.core.data.entity.*;
import cz.quantumleap.core.data.transport.SliceRequest;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.impl.DSL;

import java.util.ArrayList;
import java.util.List;

import static cz.quantumleap.core.tables.EnumValueTable.ENUM_VALUE;

public class TableSliceFieldsFactory {

    private final Entity<?> entity;
    private final EntityManager entityManager;

    public TableSliceFieldsFactory(Entity<?> entity, EntityManager entityManager) {
        this.entity = entity;
        this.entityManager = entityManager;
    }

    public List<Field<?>> forSliceRequest(SliceRequest request) {
        Field<?>[] originalFields = entity.getTable().fields();
        List<Field<?>> fields = new ArrayList<>(originalFields.length);

        for (Field<?> field : originalFields) {
            FieldMetaType fieldMetaType = entity.getFieldMetaType(field);
            if (fieldMetaType instanceof EnumMetaType) {
                Table<?> enumTable = resolveTableAlias(ENUM_VALUE, field);

                Field<?> enumField = enumTable.field(ENUM_VALUE.LABEL).as(field);

                fields.add(enumField);
            } else if (fieldMetaType instanceof SetMetaType) {
                String enumId = fieldMetaType.asSet().getEnumId();

                Field<?> setField = DSL.coalesce(
                        DSL
                                .select(DSL.groupConcat(ENUM_VALUE.LABEL).separator(", "))
                                .from(ENUM_VALUE)
                                .where(ENUM_VALUE.ENUM_ID.eq(enumId)
                                        .and(ENUM_VALUE.ID.eq(DSL.any((Field<String[]>) field)))),
                        DSL.val("")
                ).as(field);

                fields.add(setField);
            } else if (fieldMetaType instanceof LookupMetaType) {
                EntityIdentifier<?> lookupEntityIdentifier = fieldMetaType.asLookup().getEntityIdentifier();
                Entity<?> lookupEntity = entityManager.getEntity(lookupEntityIdentifier);
                Table<?> lookupTable = resolveTableAlias(lookupEntity.getTable(), field);

                String labelFieldName = field.getName() + ".label";
                Field<?> labelField = lookupEntity.buildLookupLabelFieldForTable(lookupTable).as(labelFieldName);

                fields.add(field);
                fields.add(labelField);
            } else {
                fields.add(field);
            }
        }

        return fields;
    }

    // TODO Static utils...
    private Table<?> resolveTableAlias(Table<?> table, Field<?> field) {
        String alias = "t_" + field.getName();
        return table.as(alias);
    }
}
