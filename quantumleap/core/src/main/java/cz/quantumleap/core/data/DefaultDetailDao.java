package cz.quantumleap.core.data;

import org.apache.commons.lang3.Validate;
import org.jooq.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class DefaultDetailDao<TABLE extends Table<? extends Record>> implements DetailDao<TABLE> {

    private final Table<? extends Record> table;
    private final DSLContext dslContext;
    private final MapperFactory mapperFactory;
    private final RecordAuditor recordAuditor;

    public DefaultDetailDao(Table<? extends Record> table, DSLContext dslContext, MapperFactory mapperFactory, RecordAuditor recordAuditor) {
        this.table = table;
        this.dslContext = dslContext;
        this.mapperFactory = mapperFactory;
        this.recordAuditor = recordAuditor;
    }

    // TODO Maybe create multiple API methods for two and more ids...or condition builder?
    public <T> Optional<T> fetchById(Object id, Class<T> type) {
        return fetchByCondition(createPrimaryKeyCondition(id), type);
    }

    public <T> Optional<T> fetchByCondition(Condition condition, Class<T> type) {
        return dslContext.selectFrom(table)
                .where(condition)
                .fetchOptional(mapperFactory.createTransportMapper(type));
    }

    public <T> T save(T detail) {
        Validate.notNull(detail);

        Class<T> transportType = (Class<T>) detail.getClass();

        Record record = mapperFactory
                .createTransportUnMapper(transportType)
                .unMap(detail, dslContext.newRecord(table));

        Condition[] primaryKeyCondition = createPrimaryKeyConditions(record);
        if (primaryKeyCondition != null) {
            return update(record, primaryKeyCondition, transportType);
        } else {
            return insert(record, transportType);
        }
    }

    private <T> T insert(Record record, Class<T> resultType) {

        recordAuditor.onInsert(record);

        Map<? extends Field<?>, ?> changedValues = getChangedValues(record);

        return dslContext.insertInto(table)
                .set(changedValues)
                .returning(table.fields())
                .fetchOne()
                .map(mapperFactory.createTransportMapper(resultType));
    }

    private <T> T update(Record record, Condition[] conditions, Class<T> resultType) {

        recordAuditor.onUpdate(record);

        Map<Field<?>, Object> changedValues = getChangedValues(record);

        return dslContext.update(table)
                .set(changedValues)
                .where(conditions)
                .returning(table.fields())
                .fetchOne()
                .map(mapperFactory.createTransportMapper(resultType));
    }

    private Map<Field<?>, Object> getChangedValues(Record record) {
        List<? extends TableField<? extends Record, ?>> primaryKeyFields = getPrimaryKeyFields();
        Map<Field<?>, Object> changedValues = new HashMap<>(record.size());
        for (Field<?> field : record.fields()) {
            if (record.changed(field) && !primaryKeyFields.contains(field)) {
                changedValues.put(field, record.getValue(field));
            }
        }
        return changedValues;
    }

    public void deleteById(Object id) {
        dslContext.delete(table)
                .where(createPrimaryKeyCondition(id))
                .execute();
    }

    public Field<?>[] getAssignableFields() {
        // TODO By default all of them...maybe except from audit fields? Maybe this is bogus...
        return table.fields();
    }

    // TODO To IdentifierConditionBuilder or PrimaryKeyConditionBuilder...
    private Condition[] createPrimaryKeyConditions(Record record) {
        List<? extends TableField<? extends Record, ?>> fields = getPrimaryKeyFields();
        Validate.isTrue(fields.size() > 0, "Table " + table + " does not have any primary key columns!");

        if (fields.stream().allMatch(field -> record.getValue(field) != null)) {
            return fields.stream()
                    .map(field -> createEqCondition(field, record.getValue(field)))
                    .toArray(Condition[]::new);
        }
        return null;
    }

    private Condition createPrimaryKeyCondition(Object value) {
        List<? extends TableField<? extends Record, ?>> fields = getPrimaryKeyFields();
        Validate.isTrue(fields.size() == 1, "Table " + table + " has " + fields.size() + " primary key fields, expected to be one!");
        return createEqCondition(fields.get(0), value);
    }

    private List<? extends TableField<? extends Record, ?>> getPrimaryKeyFields() {
        UniqueKey<? extends Record> primaryKey = table.getPrimaryKey();
        Validate.notNull(primaryKey, "Table " + table.getName() + " does not have primary key!");
        return primaryKey.getFields();
    }

    @SuppressWarnings("unchecked")
    private Condition createEqCondition(TableField<? extends Record, ?> field, Object value) {
        return ((TableField<? extends Record, Object>) field).eq(value);
    }
}
