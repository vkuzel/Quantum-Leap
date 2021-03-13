package cz.quantumleap.core.database.entity;

import org.apache.commons.lang3.Validate;
import org.jooq.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.Collections.singletonList;

public class Entity<TABLE extends Table<? extends Record>> {

    private final EntityIdentifier<TABLE> entityIdentifier;
    /**
     * Additional type info for entity fields. Can be null for fields which
     * does not have any extra info.
     */
    private final Map<Field<?>, FieldMetaType> fieldMetaTypeMap;
    private final List<Field<?>> primaryKeyFields;
    /**
     * Builder accepts entity-table with alias.
     */
    private final Function<Table<?>, Field<String>> lookupLabelFieldBuilder;

    private final Condition defaultFilterCondition;
    private final PrimaryKeyConditionBuilder<TABLE> primaryKeyConditionBuilder;
    private final Function<String, Condition> wordConditionBuilder;

    public Entity(
            EntityIdentifier<TABLE> entityIdentifier,
            Map<Field<?>, FieldMetaType> fieldMetaTypeMap,
            List<Field<?>> primaryKeyFields,
            Function<Table<?>, Field<String>> lookupLabelFieldBuilder,
            Condition defaultFilterCondition,
            Function<String, Condition> wordConditionBuilder
    ) {
        this.entityIdentifier = entityIdentifier;
        this.fieldMetaTypeMap = fieldMetaTypeMap;
        this.primaryKeyFields = primaryKeyFields;
        this.lookupLabelFieldBuilder = lookupLabelFieldBuilder;

        this.defaultFilterCondition = defaultFilterCondition;
        this.primaryKeyConditionBuilder = new PrimaryKeyConditionBuilder<>(this);
        this.wordConditionBuilder = wordConditionBuilder;
    }

    public EntityIdentifier<TABLE> getIdentifier() {
        return entityIdentifier;
    }

    public TABLE getTable() {
        return entityIdentifier.getTable();
    }

    public Map<Field<?>, FieldMetaType> getFieldMetaTypeMap() {
        return fieldMetaTypeMap;
    }

    public FieldMetaType getFieldMetaType(Field<?> field) {
        return fieldMetaTypeMap.get(field);
    }

    public List<Field<?>> getPrimaryKeyFields() {
        return primaryKeyFields;
    }

    public Field<?> getPrimaryKeyField() {
        if (primaryKeyFields.size() == 1) {
            return primaryKeyFields.get(0);
        } else {
            throw new IllegalStateException("Incorrect number of primary key fields for " + toString());
        }
    }

    public Field<String> getLookupLabelField() {
        return lookupLabelFieldBuilder.apply(entityIdentifier.getTable());
    }

    public Field<String> buildLookupLabelFieldForTable(Table<?> table) {
        return lookupLabelFieldBuilder.apply(table);
    }

    public Condition getDefaultFilterCondition() {
        return defaultFilterCondition;
    }

    public PrimaryKeyConditionBuilder<TABLE> getPrimaryKeyConditionBuilder() {
        return primaryKeyConditionBuilder;
    }

    public Function<String, Condition> getWordConditionBuilder() {
        return wordConditionBuilder;
    }

    public static <TABLE extends Table<? extends Record>> Builder<TABLE> builder(TABLE table) {
        return builder(EntityIdentifier.forTable(table));
    }

    public static <TABLE extends Table<? extends Record>> Builder<TABLE> builder(TABLE table, String qualifier) {
        return builder(EntityIdentifier.forTableWithQualifier(table, qualifier));
    }

    public static <TABLE extends Table<? extends Record>> Builder<TABLE> builder(EntityIdentifier<TABLE> entityIdentifier) {
        return new Builder<>(entityIdentifier);
    }

    @Override
    public String toString() {
        return entityIdentifier.toString();
    }

    public static class Builder<TABLE extends Table<? extends Record>> {

        private final EntityIdentifier<TABLE> entityIdentifier;
        private Map<Field<?>, FieldMetaType> fieldMetaTypeMap = new HashMap<>();
        private Field<?> primaryKeyField;
        private Function<Table<?>, Field<String>> lookupLabelFieldBuilder = table -> null;

        private Condition defaultFilterCondition = null;
        private Function<String, Condition> wordConditionBuilder = q -> null;

        private Builder(EntityIdentifier<TABLE> entityIdentifier) {
            Validate.notNull(entityIdentifier);
            this.entityIdentifier = entityIdentifier;
        }

        public Builder<TABLE> addEnumMetaType(Field<?> field) {
            EnumMetaType enumMetaType = new EnumMetaType(field);
            return addFieldMetaType(field, enumMetaType);
        }

        public Builder<TABLE> addEnumMetaType(Field<?> field, String enumId) {
            EnumMetaType enumMetaType = new EnumMetaType(enumId);
            return addFieldMetaType(field, enumMetaType);
        }

        public Builder<TABLE> addSetMetaType(Field<?> field) {
            SetMetaType setMetaType = new SetMetaType(field);
            return addFieldMetaType(field, setMetaType);
        }

        public Builder<TABLE> addSetMetaType(Field<?> field, String enumId) {
            SetMetaType setMetaType = new SetMetaType(enumId);
            return addFieldMetaType(field, setMetaType);
        }

        public Builder<TABLE> addLookupMetaType(Field<?> field, EntityIdentifier<?> entityIdentifier) {
            LookupMetaType lookupMetaType = new LookupMetaType(entityIdentifier);
            return addFieldMetaType(field, lookupMetaType);
        }

        public Builder<TABLE> addFieldMetaType(Field<?> field, FieldMetaType fieldMetaType) {
            FieldMetaType originalMetaType = this.fieldMetaTypeMap.put(field, fieldMetaType);
            if (originalMetaType != null) {
                throw new IllegalArgumentException("Field " + field + " already has meta type!");
            }
            return this;
        }

        public Builder<TABLE> setPrimaryKeyField(Field<?> primaryKeyField) {
            Validate.notNull(primaryKeyField);
            this.primaryKeyField = primaryKeyField;
            return this;
        }

        public Builder<TABLE> setLookupLabelField(Field<String> lookupLabelField) {
            return setLookupLabelFieldBuilder(table -> table.field(lookupLabelField));
        }

        public Builder<TABLE> setLookupLabelFieldBuilder(Function<Table<?>, Field<String>> lookupLabelFieldBuilder) {
            this.lookupLabelFieldBuilder = lookupLabelFieldBuilder;
            return this;
        }

        public Builder<TABLE> setDefaultFilterCondition(Condition defaultFilterCondition) {
            this.defaultFilterCondition = defaultFilterCondition;
            return this;
        }

        public Builder<TABLE> setWordConditionBuilder(Function<String, Condition> wordConditionBuilder) {
            this.wordConditionBuilder = wordConditionBuilder;
            return this;
        }

        public Entity<TABLE> build() {
            TABLE table = entityIdentifier.getTable();
            List<Field<?>> primaryKeyFields;
            if (this.primaryKeyField != null) {
                primaryKeyFields = singletonList(this.primaryKeyField);
            } else {
                primaryKeyFields = getPrimaryKeyFields(table);
            }
            return new Entity<>(
                    entityIdentifier,
                    fieldMetaTypeMap,
                    primaryKeyFields,
                    lookupLabelFieldBuilder,
                    defaultFilterCondition,
                    wordConditionBuilder
            );
        }

        private List<Field<?>> getPrimaryKeyFields(Table<? extends Record> table) {
            UniqueKey<? extends Record> primaryKey = table.getPrimaryKey();
            if (primaryKey != null) {
                return Collections.unmodifiableList(primaryKey.getFields());
            } else {
                return Collections.emptyList();
            }
        }
    }
}
