package cz.quantumleap.core.data.entity;

import cz.quantumleap.core.data.list.DefaultFilterBuilder;
import cz.quantumleap.core.data.list.FilterBuilder;
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
    private final List<Field<?>> primaryKeyFields;
    private final PrimaryKeyConditionBuilder<TABLE> primaryKeyConditionBuilder;
    /**
     * Builder is handy for aliased tables.
     */
    private final Function<Table<?>, Field<String>> lookupLabelFieldBuilder;
    /**
     * Additional type info for entity fields. Can be null for fields which
     * does not have any extra info.
     */
    private final Map<Field<?>, FieldMetaType> fieldMetaTypeMap;
    private final FilterBuilder filterBuilder;

    public Entity(
            EntityIdentifier<TABLE> entityIdentifier,
            List<Field<?>> primaryKeyFields,
            Function<Table<?>, Field<String>> lookupLabelFieldBuilder,
            Map<Field<?>, FieldMetaType> fieldMetaTypeMap,
            FilterBuilder filterBuilder
    ) {
        this.entityIdentifier = entityIdentifier;
        this.primaryKeyFields = primaryKeyFields;
        this.lookupLabelFieldBuilder = lookupLabelFieldBuilder;
        this.primaryKeyConditionBuilder = new PrimaryKeyConditionBuilder<>(this);
        this.fieldMetaTypeMap = fieldMetaTypeMap;

        this.filterBuilder = filterBuilder;
    }

    public EntityIdentifier<TABLE> getIdentifier() {
        return entityIdentifier;
    }

    public TABLE getTable() {
        return entityIdentifier.getTable();
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

    public PrimaryKeyConditionBuilder<TABLE> getPrimaryKeyConditionBuilder() {
        return primaryKeyConditionBuilder;
    }

    public Field<String> getLookupLabelField() {
        return lookupLabelFieldBuilder.apply(entityIdentifier.getTable());
    }

    public Field<String> buildLookupLabelFieldForTable(Table<?> table) {
        return lookupLabelFieldBuilder.apply(table);
    }

    public Map<Field<?>, FieldMetaType> getFieldMetaTypeMap() {
        return fieldMetaTypeMap;
    }

    public FieldMetaType getFieldMetaType(Field<?> field) {
        return fieldMetaTypeMap.get(field);
    }

    public FilterBuilder getFilterBuilder() {
        return filterBuilder;
    }

    public static <TABLE extends Table<? extends Record>> Builder<TABLE> createBuilder(TABLE table) {
        return createBuilder(EntityIdentifier.forTable(table));
    }

    public static <TABLE extends Table<? extends Record>> Builder<TABLE> createBuilder(TABLE table, String qualifier) {
        return createBuilder(EntityIdentifier.forTableWithQualifier(table, qualifier));
    }

    public static <TABLE extends Table<? extends Record>> Builder<TABLE> createBuilder(EntityIdentifier<TABLE> entityIdentifier) {
        return new Builder<>(entityIdentifier);
    }

    @Override
    public String toString() {
        return entityIdentifier.toString();
    }

    public static class Builder<TABLE extends Table<? extends Record>> {

        private final EntityIdentifier<TABLE> entityIdentifier;
        private Field<?> primaryKeyField;
        private Function<Table<?>, Field<String>> lookupLabelFieldBuilder = table -> null;
        private Condition defaultFilterCondition = null;
        private Map<Field<?>, FieldMetaType> fieldMetaTypeMap = new HashMap<>();
        private Function<String, Condition> wordConditionBuilder = s -> null;

        public Builder(EntityIdentifier<TABLE> entityIdentifier) {
            Validate.notNull(entityIdentifier);
            this.entityIdentifier = entityIdentifier;
        }

        public Builder<TABLE> setPrimaryKeyField(Field<?> primaryKeyField) {
            Validate.notNull(primaryKeyField);
            this.primaryKeyField = primaryKeyField;
            return this;
        }

        public Builder<TABLE> setDefaultFilterCondition(Condition defaultFilterCondition) {
            this.defaultFilterCondition = defaultFilterCondition;
            return this;
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

        public Builder<TABLE> setLookupLabelField(Field<String> lookupLabelField) {
            return setLookupLabelFieldBuilder(table -> lookupLabelField);
        }

        public Builder<TABLE> setLookupLabelFieldBuilder(Function<Table<?>, Field<String>> lookupLabelFieldBuilder) {
            this.lookupLabelFieldBuilder = lookupLabelFieldBuilder;
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
                    primaryKeyFields,
                    lookupLabelFieldBuilder,
                    fieldMetaTypeMap,
                    new DefaultFilterBuilder(table, defaultFilterCondition, wordConditionBuilder)
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
