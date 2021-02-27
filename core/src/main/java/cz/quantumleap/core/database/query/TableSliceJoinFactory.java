package cz.quantumleap.core.database.query;

import cz.quantumleap.core.database.EntityRegistry;
import cz.quantumleap.core.database.domain.SliceRequest;
import cz.quantumleap.core.database.entity.*;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.jooq.Table;

import java.util.function.Function;

import static cz.quantumleap.core.database.query.QueryUtils.resolveTableAlias;
import static cz.quantumleap.core.tables.EnumValueTable.ENUM_VALUE;

public final class TableSliceJoinFactory {

    private final Entity<?> entity;
    private final EntityRegistry entityRegistry;

    public TableSliceJoinFactory(Entity<?> entity, EntityRegistry entityRegistry) {
        this.entity = entity;
        this.entityRegistry = entityRegistry;
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
                Entity<?> lookupEntity = entityRegistry.getEntity(lookupEntityIdentifier);
                Table<?> lookupTable = resolveTableAlias(lookupEntity.getTable(), field);
                Field<Object> lookupPrimaryKey = getTypedField(lookupEntity.getPrimaryKeyField(), Object.class);
                Field<Object> aliasedLookupPrimaryKey = getFieldSafely(lookupTable, lookupPrimaryKey);

                selectJoinStep = selectJoinStep
                        .leftJoin(lookupTable)
                        .on(aliasedLookupPrimaryKey.eq(field));
            }
        }
        return selectJoinStep;
    }

    private <T> Field<T> getFieldSafely(Table<?> table, Field<T> field) {
        Field<T> safeField = table.field(field);
        if (safeField != null) {
            return safeField;
        } else {
            throw new IllegalArgumentException("Field " + field + " not found in table " + table);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> Field<T> getTypedField(Field<?> field, Class<T> type) {
        if (type.isAssignableFrom(field.getType())) {
            return (Field<T>) field;
        } else {
            String msg = "Field " + field + " of type " + field.getType() + " cannot be cast to type " + type;
            throw new IllegalArgumentException(msg);
        }
    }
}
