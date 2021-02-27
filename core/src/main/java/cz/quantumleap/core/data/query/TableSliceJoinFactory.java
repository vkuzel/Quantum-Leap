package cz.quantumleap.core.data.query;

import cz.quantumleap.core.data.EntityManager;
import cz.quantumleap.core.data.entity.*;
import cz.quantumleap.core.data.transport.SliceRequest;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.jooq.Table;

import java.util.function.Function;

import static cz.quantumleap.core.data.query.QueryUtils.resolveTableAlias;
import static cz.quantumleap.core.tables.EnumValueTable.ENUM_VALUE;

public final class TableSliceJoinFactory {

    private final Entity<?> entity;
    private final EntityManager entityManager;

    public TableSliceJoinFactory(Entity<?> entity, EntityManager entityManager) {
        this.entity = entity;
        this.entityManager = entityManager;
    }

    @SuppressWarnings("unused")
    public Function<SelectJoinStep<Record>, SelectJoinStep<Record>> forSliceRequest(SliceRequest sliceRequest) {
        return this::apply;
    }

    private SelectJoinStep<Record> apply(SelectJoinStep<Record> selectJoinStep) {
        Field<?>[] fields = entity.getTable().fields();
        for (Field<?> field : fields) {
            FieldMetaType fieldMetaType = entity.getFieldMetaType(field);
            if (fieldMetaType instanceof EnumMetaType) {
                Table<?> enumTable = resolveTableAlias(ENUM_VALUE, field);
                String enumId = fieldMetaType.asEnum().getEnumId();
                Field<String> enumField = getTypedField(field, String.class);

                selectJoinStep = selectJoinStep
                        .leftJoin(enumTable)
                        .on(getFieldSafely(enumTable, ENUM_VALUE.ENUM_ID).eq(enumId)
                                .and(getFieldSafely(enumTable, ENUM_VALUE.ID).eq(enumField)));
            } else if (fieldMetaType instanceof LookupMetaType) {
                EntityIdentifier<?> lookupEntityIdentifier = fieldMetaType.asLookup().getEntityIdentifier();
                Entity<?> lookupEntity = entityManager.getEntity(lookupEntityIdentifier);
                Table<?> lookupTable = resolveTableAlias(lookupEntity.getTable(), field);
                Field<Object> lookupPrimaryKey = getTypedField(lookupEntity.getPrimaryKeyField(), Object.class);

                selectJoinStep = selectJoinStep
                        .leftJoin(lookupTable)
                        .on(lookupPrimaryKey.eq(field));
            }
        }
        return selectJoinStep;
    }

    private <T> Field<T> getFieldSafely(Table<?> table, Field<T> field) {
        Field<T> safeField = table.field(field);
        if (safeField == null) {
            throw new IllegalArgumentException("Field " + field + " not found in table " + table);
        }
        return safeField;
    }

    @SuppressWarnings("unchecked")
    private <T> Field<T> getTypedField(Field<?> field, Class<T> type) {
        if (field.getType() != type) {
            String msg = "Field " + field + " of type " + field.getType() + " cannot be case to type " + type;
            throw new IllegalArgumentException(msg);
        }
        return (Field<T>) field;
    }
}
