package cz.quantumleap.core.data.entity;

import cz.quantumleap.core.data.list.*;
import cz.quantumleap.core.data.primarykey.PrimaryKeyConditionBuilder;
import cz.quantumleap.core.data.primarykey.PrimaryKeyResolver;
import cz.quantumleap.core.data.primarykey.TablePrimaryKeyResolver;
import org.apache.commons.lang3.Validate;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Entity<TABLE extends Table<? extends Record>> {

    private final EntityIdentifier<TABLE> entityIdentifier;
    private final PrimaryKeyResolver primaryKeyResolver;
    private final PrimaryKeyConditionBuilder primaryKeyConditionBuilder;
    private final Field<String> lookupLabelField;
    private final Map<Field<?>, EntityIdentifier<?>> lookupFieldsMap;
    private final FilterBuilder filterBuilder;
    private final SortingBuilder sortingBuilder;
    private final LimitBuilder limitBuilder;

    public Entity(EntityIdentifier<TABLE> entityIdentifier, PrimaryKeyResolver primaryKeyResolver, PrimaryKeyConditionBuilder primaryKeyConditionBuilder, Field<String> lookupLabelField, Map<Field<?>, EntityIdentifier<?>> lookupFieldsMap, FilterBuilder filterBuilder, SortingBuilder sortingBuilder, LimitBuilder limitBuilder) {
        this.entityIdentifier = entityIdentifier;
        this.primaryKeyResolver = primaryKeyResolver;
        this.primaryKeyConditionBuilder = primaryKeyConditionBuilder;
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

    public PrimaryKeyResolver getPrimaryKeyResolver() {
        return primaryKeyResolver;
    }

    public PrimaryKeyConditionBuilder getPrimaryKeyConditionBuilder() {
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
        private PrimaryKeyResolver primaryKeyResolver;
        private Condition defaultFilterCondition = null;
        private Map<Field<?>, EntityIdentifier<?>> lookupFieldsMap = new HashMap<>();
        private Field<String> lookupLabelField = null;
        private Function<String, Condition> wordConditionBuilder = s -> null;
        private SortingBuilder sortingBuilder = null;

        public Builder(EntityIdentifier<TABLE> entityIdentifier) {
            Validate.notNull(entityIdentifier);
            this.entityIdentifier = entityIdentifier;
        }

        public Builder<TABLE> setPrimaryKeyResolver(PrimaryKeyResolver primaryKeyResolver) {
            this.primaryKeyResolver = primaryKeyResolver;
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
            PrimaryKeyResolver primaryKeyResolver;
            if (this.primaryKeyResolver != null) {
                primaryKeyResolver = this.primaryKeyResolver;
            } else {
                primaryKeyResolver = new TablePrimaryKeyResolver(table);
            }
            SortingBuilder sortingBuilder;
            if (this.sortingBuilder != null) {
                sortingBuilder = this.sortingBuilder;
            } else {
                sortingBuilder = new DefaultSortingBuilder(table, lookupLabelField);
            }
            return new Entity<>(
                    entityIdentifier,
                    primaryKeyResolver,
                    new PrimaryKeyConditionBuilder(primaryKeyResolver),
                    lookupLabelField,
                    lookupFieldsMap,
                    new DefaultFilterBuilder(table, defaultFilterCondition, wordConditionBuilder),
                    sortingBuilder,
                    LimitBuilder.DEFAULT
            );
        }
    }
}
