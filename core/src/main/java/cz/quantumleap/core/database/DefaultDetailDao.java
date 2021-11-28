package cz.quantumleap.core.database;

import cz.quantumleap.core.database.entity.Entity;
import org.apache.commons.lang3.Validate;
import org.jooq.*;
import org.jooq.Record;

import java.util.*;

public final class DefaultDetailDao<TABLE extends Table<? extends Record>> implements DetailDao<TABLE> {

    private final Entity<TABLE> entity;
    private final DSLContext dslContext;

    public DefaultDetailDao(Entity<TABLE> entity, DSLContext dslContext) {
        this.entity = entity;
        this.dslContext = dslContext;
    }

    @Override
    public Entity<TABLE> getDetailEntity() {
        return entity;
    }

    @Override
    public <T> T fetchById(Object id, Class<T> type) {
        Condition condition = entity.getPrimaryKeyConditionBuilder().buildFromId(id);
        return fetchByCondition(condition, type);
    }

    @Override
    public <T> T fetchByCondition(Condition condition, Class<T> type) {
        Table<? extends Record> table = entity.getTable();
        return dslContext.selectFrom(table)
                .where(condition)
                .fetchOneInto(type);
    }

    @Override
    public <T> T save(T detail) {
        Validate.notNull(detail);
        return saveAll(Collections.singletonList(detail)).get(0);
    }

    @Override
    public <T> List<T> saveAll(List<T> details) {
        if (details.isEmpty()) {
            return Collections.emptyList();
        }

        Class<T> detailType = getDetailClass(details.get(0));
        List<Record> records = new ArrayList<>(details.size());
        RecordFactory<T> recordFactory = new RecordFactory<>(dslContext, detailType, entity.getTable());

        for (T detail : details) {
            Record record = recordFactory.createRecord(detail);
            records.add(record);
        }

        return saveRecords(records, detailType);
    }

    @SuppressWarnings("unchecked")
    private <T> Class<T> getDetailClass(T detail) {
        return (Class<T>) detail.getClass();
    }

    private <T> List<T> saveRecords(List<Record> records, Class<T> detailType) {
        List<T> results = new ArrayList<>(records.size());

        // TODO Implement bulk insert using https://www.jooq.org/doc/3.12/manual/sql-building/sql-statements/insert-statement/insert-values/
        // TODO Preserve order.
        for (Record record : records) {
            Condition condition = entity.getPrimaryKeyConditionBuilder().buildFromRecord(record);
            if (condition != null) {
                results.add(update(record, condition, detailType));
            } else {
                results.add(insert(record, detailType));
            }
        }

        return results;
    }

    private <T> T insert(Record record, Class<T> resultType) {
        RecordAuditor.getInstance().onInsert(record);

        Map<? extends Field<?>, ?> changedValues = getChangedValues(record);

        Table<? extends Record> table = entity.getTable();
        return dslContext.insertInto(table)
                .set(changedValues)
                .returning(table.fields())
                .fetchOptional()
                .map(r -> r.into(resultType))
                .orElseThrow();
    }

    private <T> T update(Record record, Condition condition, Class<T> resultType) {
        RecordAuditor.getInstance().onUpdate(record);

        Map<Field<?>, Object> changedValues = getChangedValues(record);

        Table<? extends Record> table = entity.getTable();
        return dslContext.update(table)
                .set(changedValues)
                .where(condition)
                .returning(table.fields())
                .fetchOptional()
                .map(r -> r.into(resultType))
                .orElseThrow();
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

    @Override
    public int deleteById(Object id) {
        Condition condition = entity.getPrimaryKeyConditionBuilder().buildFromId(id);
        return deleteByCondition(condition);
    }

    @Override
    public int deleteByCondition(Condition condition) {
        Table<? extends Record> table = entity.getTable();
        return dslContext.delete(table)
                .where(condition)
                .execute();
    }

    @Override
    public <T, F> List<T> saveDetailsAssociatedBy(TableField<?, F> foreignKey, F foreignId, Collection<T> details, Class<T> detailType) {
        Table<? extends Record> table = entity.getTable();
        Field<?> primaryKeyField = entity.getPrimaryKeyField();
        RecordFactory<T> recordFactory = new RecordFactory<>(dslContext, detailType, table);

        List<Record> records = new ArrayList<>(details.size());
        Set<Object> ids = new HashSet<>(details.size());

        for (T detail : details) {
            Record record = recordFactory.createRecord(detail);
            record.set(foreignKey, foreignId);
            records.add(record);
            ids.add(record.get(primaryKeyField));
        }

        dslContext.delete(table)
                .where(foreignKey.eq(foreignId).andNot(primaryKeyField.in(ids)))
                .execute();

        return saveRecords(records, detailType);
    }
}
