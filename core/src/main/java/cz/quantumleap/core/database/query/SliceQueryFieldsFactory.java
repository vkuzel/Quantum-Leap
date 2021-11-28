package cz.quantumleap.core.database.query;

import cz.quantumleap.core.database.EntityRegistry;
import cz.quantumleap.core.database.entity.*;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static cz.quantumleap.core.database.query.QueryUtils.resolveLookupFieldName;
import static cz.quantumleap.core.database.query.QueryUtils.resolveTableAlias;
import static cz.quantumleap.core.tables.EnumValueTable.ENUM_VALUE;

public class SliceQueryFieldsFactory {

    private final Entity<?> entity;
    private final EntityRegistry entityRegistry;

    public SliceQueryFieldsFactory(Entity<?> entity, EntityRegistry entityRegistry) {
        this.entity = entity;
        this.entityRegistry = entityRegistry;
    }

    public QueryFields createQueryFields() {
        List<Field<?>> entityFields = entity.getFields();

        List<Function<SelectJoinStep<Record>, SelectJoinStep<Record>>> joinTables = new ArrayList<>();
        Map<String, Field<?>> queryFieldMap = new HashMap<>(entityFields.size());
        Map<String, Field<?>> filterFieldMap = new HashMap<>(entityFields.size());
        Map<String, Field<?>> orderFieldMap = new HashMap<>(entityFields.size());

        for (Field<?> field : entityFields) {
            FieldMetaType fieldMetaType = entity.getFieldMetaType(field);
            if (fieldMetaType instanceof EnumMetaType) {
                Table<?> enumTable = resolveTableAlias(ENUM_VALUE, field);
                String enumId = fieldMetaType.asEnum().getEnumId();

                Field<String> enumField = getTypedField(field, String.class);
                Field<?> labelField = getFieldSafely(enumTable, ENUM_VALUE.LABEL).as(field);

                putFieldToMap(labelField, queryFieldMap);
                putFieldToMap(labelField, filterFieldMap);
                putFieldToMap(labelField, orderFieldMap);
                addLeftJoin(
                        enumTable,
                        getFieldSafely(enumTable, ENUM_VALUE.ENUM_ID).eq(enumId)
                                .and(getFieldSafely(enumTable, ENUM_VALUE.ID).eq(enumField)),
                        joinTables
                );
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

                putFieldToMap(setField, queryFieldMap);
                putFieldToMap(setField, filterFieldMap);
                putFieldToMap(setField, orderFieldMap);
            } else if (fieldMetaType instanceof LookupMetaType) {
                EntityIdentifier<?> lookupEntityIdentifier = fieldMetaType.asLookup().getEntityIdentifier();
                Entity<?> lookupEntity = entityRegistry.getLookupEntity(lookupEntityIdentifier);
                Table<?> lookupTable = resolveTableAlias(lookupEntity.getTable(), field);

                String lookupFieldName = resolveLookupFieldName(field);
                Field<?> lookupField = lookupEntity.buildLookupLabelFieldForTable(lookupTable);
                Field<Object> lookupPrimaryKey = getTypedField(lookupEntity.getPrimaryKeyField(), Object.class);
                Field<Object> aliasedLookupPrimaryKey = getFieldSafely(lookupTable, lookupPrimaryKey);

                putFieldToMap(field, queryFieldMap);
                putFieldToMap(lookupField.as(lookupFieldName), queryFieldMap);
                putFieldToMap(field, filterFieldMap);
                putFieldToMap(lookupFieldName, lookupField, filterFieldMap);
                putFieldToMap(lookupFieldName, lookupField, orderFieldMap);
                addLeftJoin(
                        lookupTable,
                        aliasedLookupPrimaryKey.eq(field),
                        joinTables
                );
            } else {
                putFieldToMap(field, queryFieldMap);
                putFieldToMap(field, filterFieldMap);
                putFieldToMap(field, orderFieldMap);
            }
        }

        return new QueryFields(joinTables, queryFieldMap, filterFieldMap, orderFieldMap);
    }

    private void addLeftJoin(
            Table<?> table,
            Condition condition,
            List<Function<SelectJoinStep<Record>, SelectJoinStep<Record>>> joinTables
    ) {
        joinTables.add(selectJoinStep -> selectJoinStep.leftJoin(table).on(condition));
    }

    private void putFieldToMap(Field<?> field, Map<String, Field<?>> fieldMap) {
        putFieldToMap(field.getName(), field, fieldMap);
    }

    private void putFieldToMap(String fieldName, Field<?> field, Map<String, Field<?>> fieldMap) {
        fieldMap.put(fieldName, field);
    }

    @SuppressWarnings("unchecked")
    private Field<String[]> toArrayField(Field<?> field) {
        return (Field<String[]>) field;
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

    public static class QueryFields {

        private final List<Function<SelectJoinStep<Record>, SelectJoinStep<Record>>> joinTables;
        private final Map<String, Field<?>> queryFieldMap;
        private final Map<String, Field<?>> conditionFieldMap;
        private final Map<String, Field<?>> orderFieldMap;

        public QueryFields(
                List<Function<SelectJoinStep<Record>, SelectJoinStep<Record>>> joinTables,
                Map<String, Field<?>> queryFieldMap,
                Map<String, Field<?>> conditionFieldMap,
                Map<String, Field<?>> orderFieldMap
        ) {
            this.joinTables = joinTables;
            this.queryFieldMap = queryFieldMap;
            this.conditionFieldMap = conditionFieldMap;
            this.orderFieldMap = orderFieldMap;
        }

        public List<Function<SelectJoinStep<Record>, SelectJoinStep<Record>>> getJoinTables() {
            return joinTables;
        }

        public Map<String, Field<?>> getQueryFieldMap() {
            return queryFieldMap;
        }

        public Map<String, Field<?>> getConditionFieldMap() {
            return conditionFieldMap;
        }

        public Map<String, Field<?>> getOrderFieldMap() {
            return orderFieldMap;
        }
    }
}
