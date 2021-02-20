package cz.quantumleap.core.data.query;

import cz.quantumleap.core.data.EntityManager;
import cz.quantumleap.core.data.entity.*;
import cz.quantumleap.core.data.transport.SliceRequest;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.jooq.Table;

import java.util.function.Function;

import static cz.quantumleap.core.tables.EnumValueTable.ENUM_VALUE;

public class TableSliceJoinFactory {

    private final Entity<?> entity;
    private final EntityManager entityManager;

    public TableSliceJoinFactory(Entity<?> entity, EntityManager entityManager) {
        this.entity = entity;
        this.entityManager = entityManager;
    }

    public Function<SelectJoinStep<Record>, SelectJoinStep<Record>> forSliceRequest(SliceRequest request) {
        return this::apply;
    }

    private SelectJoinStep<Record> apply(SelectJoinStep selectJoinStep) {
        Field<?>[] fields = entity.getTable().fields();
        for (Field<?> field : fields) {
            FieldMetaType fieldMetaType = entity.getFieldMetaType(field);
            if (fieldMetaType instanceof EnumMetaType) {
                Table<?> enumTable = resolveTableAlias(ENUM_VALUE, field);
                String enumId = fieldMetaType.asEnum().getEnumId();
                Field<String> enumField = (Field<String>) field;

                selectJoinStep = selectJoinStep
                        .leftJoin(enumTable)
                        .on(enumTable.field(ENUM_VALUE.ENUM_ID).eq(enumId)
                                .and(enumTable.field(ENUM_VALUE.ID).eq(enumField)));
            } else if (fieldMetaType instanceof LookupMetaType) {
                EntityIdentifier<?> lookupEntityIdentifier = fieldMetaType.asLookup().getEntityIdentifier();
                Entity<?> lookupEntity = entityManager.getEntity(lookupEntityIdentifier);
                Table<?> lookupTable = resolveTableAlias(lookupEntity.getTable(), field);
                Field<?> lookupPrimaryKey = lookupEntity.getPrimaryKeyField();

                selectJoinStep = selectJoinStep
                        .leftJoin(lookupTable)
                        .on(((Field<Object>) lookupTable.field(lookupPrimaryKey)).eq(field));
            }
        }
        return selectJoinStep;

    }

    // TODO Static utils...
    private Table<?> resolveTableAlias(Table<?> table, Field<?> field) {
        String alias = "t_" + field.getName();
        return table.as(alias);
    }
}
