package cz.quantumleap.core.data;

import cz.quantumleap.core.data.mapper.MapperFactory;
import cz.quantumleap.core.data.primarykey.PrimaryKeyConditionBuilder;
import org.apache.commons.lang3.Validate;
import org.jooq.*;

import java.util.*;

public final class DefaultDetailDao<TABLE extends Table<? extends Record>> implements DetailDao<TABLE> {

    private final Table<? extends Record> table;
    private final DSLContext dslContext;
    private final PrimaryKeyConditionBuilder primaryKeyConditionBuilder;
    private final MapperFactory mapperFactory;
    private final RecordAuditor recordAuditor;

    public DefaultDetailDao(Table<? extends Record> table, DSLContext dslContext, PrimaryKeyConditionBuilder primaryKeyConditionBuilder, MapperFactory mapperFactory, RecordAuditor recordAuditor) {
        this.table = table;
        this.dslContext = dslContext;
        this.primaryKeyConditionBuilder = primaryKeyConditionBuilder;
        this.mapperFactory = mapperFactory;
        this.recordAuditor = recordAuditor;
    }

    public <T> Optional<T> fetchById(Object id, Class<T> type) {
        Condition condition = primaryKeyConditionBuilder.buildFromId(id);
        return fetchByCondition(condition, type);
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

        Optional<Condition> condition = primaryKeyConditionBuilder.buildFromRecord(record);
        if (condition.isPresent()) {
            return update(record, condition.get(), transportType);
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

    private <T> T update(Record record, Condition condition, Class<T> resultType) {
        recordAuditor.onUpdate(record);

        Map<Field<?>, Object> changedValues = getChangedValues(record);

        return dslContext.update(table)
                .set(changedValues)
                .where(condition)
                .returning(table.fields())
                .fetchOne()
                .map(mapperFactory.createTransportMapper(resultType));
    }

    private Map<Field<?>, Object> getChangedValues(Record record) {
        Map<Field<?>, Object> changedValues = new HashMap<>(record.size());
        for (Field<?> field : record.fields()) {
            if (record.changed(field)) {
                changedValues.put(field, record.getValue(field));
            }
        }
        return changedValues;
    }

    public void deleteById(Object id) {
        Condition condition = primaryKeyConditionBuilder.buildFromId(id);
        dslContext.delete(table)
                .where(condition)
                .execute();
    }

    public <T> List<T> saveDetailsAssociatedBy(TableField foreignKey, Object foreignId, Collection<T> details, Class<T> detailType) {
        MapperFactory.TransportUnMapper<T> unMapper = mapperFactory.createTransportUnMapper(detailType);
        Field<Object> primaryKeyField = primaryKeyConditionBuilder.getPrimaryKeyField();

        List<Record> records = new ArrayList<>(details.size());
        Set<Object> ids = new HashSet<>(details.size());
        List<T> result = new ArrayList<>(details.size());

        for (T detail : details) {
            Record record = unMapper.unMap(detail, dslContext.newRecord(table));
            record.set(foreignKey, foreignId);
            records.add(record);
            ids.add(record.get(primaryKeyField));
        }

        dslContext.delete(table)
                .where(foreignKey.eq(foreignId).andNot(primaryKeyField.in(ids)))
                .execute();

        for (Record record : records) {
            Optional<Condition> condition = primaryKeyConditionBuilder.buildFromRecord(record);
            if (condition.isPresent()) {
                result.add(update(record, condition.get(), detailType));
            } else {
                result.add(insert(record, detailType));
            }
        }

        return result;
    }
}
