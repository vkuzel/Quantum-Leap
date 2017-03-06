package cz.quantumleap.core.persistence.dao;

import cz.quantumleap.core.persistence.RecordAuditor;
import org.apache.commons.lang3.Validate;
import org.jooq.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultCrudDao implements CrudDao {

    protected final Table<? extends Record> table;
    protected final DSLContext dslContext;
    protected final MapperFactory mapperFactory;
    protected final RecordAuditor recordAuditor;

    public DefaultCrudDao(Table<? extends Record> table, DSLContext dslContext, MapperFactory mapperFactory, RecordAuditor recordAuditor) {
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

    public <T> T save(T transport) {
        Validate.notNull(transport);

        Class<T> transportType = (Class<T>) transport.getClass();

        Record record = mapperFactory
                .createTransportUnMapper(transportType)
                .unMap(transport, dslContext.newRecord(table));

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

        Map<? extends Field<?>, ?> changedValues = getChangedValues(record);

        return dslContext.update(table)
                .set(changedValues)
                .where(conditions)
                .returning(table.fields())
                .fetchOne()
                .map(mapperFactory.createTransportMapper(resultType));
    }

    private Map<? extends Field<?>, ?> getChangedValues(Record record) {
        return Stream.of(record.fields())
                .filter(record::changed)
                .collect(Collectors.toMap(
                        field -> field,
                        record::getValue
                ));
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
