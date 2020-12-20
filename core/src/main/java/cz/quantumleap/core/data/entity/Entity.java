package cz.quantumleap.core.data.entity;

import cz.quantumleap.core.data.list.DefaultFilterBuilder;
import cz.quantumleap.core.data.list.DefaultSortingBuilder;
import cz.quantumleap.core.data.list.FilterBuilder;
import cz.quantumleap.core.data.list.LimitBuilder;
import cz.quantumleap.core.data.list.SortingBuilder;
import org.apache.commons.lang3.Validate;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.UniqueKey;

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
    private final Field<String> lookupLabelField;
    private final Map<Field<?>, EntityIdentifier<?>> lookupFieldsMap;
    private final FilterBuilder filterBuilder;
    private final SortingBuilder sortingBuilder;
    private final LimitBuilder limitBuilder;

    public Entity(
            EntityIdentifier<TABLE> entityIdentifier,
            List<Field<?>> primaryKeyFields,
            Field<String> lookupLabelField,
            Map<Field<?>, EntityIdentifier<?>> lookupFieldsMap,
            FilterBuilder filterBuilder,
            SortingBuilder sortingBuilder,
            LimitBuilder limitBuilder
    ) {
        this.entityIdentifier = entityIdentifier;
        this.primaryKeyFields = primaryKeyFields;
        this.primaryKeyConditionBuilder = new PrimaryKeyConditionBuilder<>(this);
        this.lookupLabelField = lookupLabelField;
        this.lookupFieldsMap = lookupFieldsMap;
        this.filterBuilder = filterBuilder;
        this.sortingBuilder = sortingBuilder;
        this.limitBuilder = limitBuilder;
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
        return lookupLabelField;
    }

    public Map<Field<?>, EntityIdentifier<?>> getLookupFieldsMap() {
        return lookupFieldsMap;
    }

    public FilterBuilder getFilterBuilder() {
        return filterBuilder;
    }

    public SortingBuilder getSortingBuilder() {
        return sortingBuilder;
    }

    public LimitBuilder getLimitBuilder() {
        return limitBuilder;
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
        private Condition defaultFilterCondition = null;
        /**
         * Describes a dependency between foreign-key fields of this entity and
         * other entities, which may be qualified.
         */
        private Map<Field<?>, EntityIdentifier<?>> lookupFieldsMap = new HashMap<>();
        private Field<String> lookupLabelField = null;
        private Function<String, Condition> wordConditionBuilder = s -> null;
        private SortingBuilder sortingBuilder = null;

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

        public Builder<TABLE> addFieldLookupMapping(Field<?> field, EntityIdentifier<?> entityIdentifier) {
            this.lookupFieldsMap.put(field, entityIdentifier);
            return this;
        }

        public Builder<TABLE> setLookupLabelField(Field<String> lookupLabelField) {
            this.lookupLabelField = lookupLabelField;
            return this;
        }

        public Builder<TABLE> setWordConditionBuilder(Function<String, Condition> wordConditionBuilder) {
            this.wordConditionBuilder = wordConditionBuilder;
            return this;
        }

        public Builder<TABLE> setSortingBuilder(SortingBuilder sortingBuilder) {
            this.sortingBuilder = sortingBuilder;
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
            SortingBuilder sortingBuilder;
            if (this.sortingBuilder != null) {
                sortingBuilder = this.sortingBuilder;
            } else {
                sortingBuilder = new DefaultSortingBuilder(table, lookupLabelField);
            }
            return new Entity<>(
                    entityIdentifier,
                    primaryKeyFields,
                    lookupLabelField,
                    lookupFieldsMap,
                    new DefaultFilterBuilder(table, defaultFilterCondition, wordConditionBuilder),
                    sortingBuilder,
                    LimitBuilder.DEFAULT
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
