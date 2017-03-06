package cz.quantumleap.core.persistence.dao;

import org.apache.commons.lang3.Validate;
import org.jooq.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class DefaultCrudDao implements CrudDao {

    protected final Table<Record> table;
    protected final DSLContext dslContext;
    protected final MapperFactory mapperFactory;

    public DefaultCrudDao(Table<Record> table, DSLContext dslContext, MapperFactory mapperFactory) {
        this.table = table;
        this.dslContext = dslContext;
        this.mapperFactory = mapperFactory;
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

        Record changesOnly = filterChangedFields(record);
        Condition[] primaryKeyCondition = createPrimaryKeyConditions(record);

        if (primaryKeyCondition != null) {
            return update(changesOnly, primaryKeyCondition, transportType);
        } else {
            return insert(changesOnly, transportType);
        }
    }

    private <T> T insert(Record record, Class<T> resultType) {
        return dslContext.insertInto(table, record.fields())
                .values(record.intoArray())
                .returning(table.fields())
                .fetchOne()
                .map(mapperFactory.createTransportMapper(resultType));
    }

    private <T> T update(Record record, Condition[] conditions, Class<T> resultType) {
        return dslContext.update(table)
                .set(record)
                .where(conditions)
                .returning(table.fields())
                .fetchOne()
                .map(mapperFactory.createTransportMapper(resultType));
    }

    public void deleteById(Object id) {
        dslContext.delete(table)
                .where(createPrimaryKeyCondition(id))
                .execute();
    }

    private Record filterChangedFields(Record record) {
        Field<?>[] changedFields = Stream.of(record.fields())
                .filter(record::changed)
                .toArray(size -> new Field[size]);

        return record.into(changedFields);
    }

    public Field<?>[] getAssignableFields() {
        // TODO By default all of them...maybe except from audit fields? Maybe this is bogus...
        return table.fields();
    }

    // TODO To IdentifierConditionBuilder or PrimaryKeyConditionBuilder...
    private Condition[] createPrimaryKeyConditions(Record record) {
        List<TableField<Record, ?>> fields = getPrimaryKeyFields();
        Validate.isTrue(fields.size() > 0, "Table " + table + " does not have any primary key columns!");

        if (fields.stream().allMatch(field -> record.getValue(field) != null)) {
            return fields.stream()
                    .map(field -> createEqCondition(field, record.getValue(field)))
                    .toArray(Condition[]::new);
        }
        return null;
    }

    private Condition createPrimaryKeyCondition(Object value) {
        List<TableField<Record, ?>> fields = getPrimaryKeyFields();
        Validate.isTrue(fields.size() == 1, "Table " + table + " has " + fields.size() + " primary key fields, expected to be one!");
        return createEqCondition(fields.get(0), value);
    }

    private List<TableField<Record, ?>> getPrimaryKeyFields() {
        UniqueKey<Record> primaryKey = table.getPrimaryKey();
        Validate.notNull(primaryKey, "Table " + table.getName() + " does not have primary key!");
        return primaryKey.getFields();
    }

    @SuppressWarnings("unchecked")
    private Condition createEqCondition(TableField<Record, ?> field, Object value) {
        return ((TableField<Record, Object>) field).eq(value);
    }
}
