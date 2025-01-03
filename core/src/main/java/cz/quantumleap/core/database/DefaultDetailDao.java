package cz.quantumleap.core.database;

import cz.quantumleap.core.database.entity.Entity;
import org.jooq.Record;
import org.jooq.*;

import java.util.*;

import static java.util.Objects.requireNonNull;

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
        var condition = entity.getPrimaryKeyConditionBuilder().buildFromId(id);
        return fetchByCondition(condition, type);
    }

    @Override
    public <T> T fetchByCondition(Condition condition, Class<T> type) {
        var table = entity.getTable();
        return dslContext.selectFrom(table)
                .where(condition)
                .fetchOneInto(type);
    }

    @Override
    public <T> T save(T detail) {
        requireNonNull(detail);
        return saveAll(Collections.singletonList(detail)).get(0);
    }

    @Override
    public <T> List<T> saveAll(List<T> details) {
        if (details.isEmpty()) {
            return Collections.emptyList();
        }

        var detailType = getDetailClass(details.get(0));
        List<Record> records = new ArrayList<>(details.size());
        var recordFactory = new RecordFactory<T>(dslContext, detailType, entity.getTable());

        for (var detail : details) {
            var record = recordFactory.createRecord(detail);
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
        for (var record : records) {
            var condition = entity.getPrimaryKeyConditionBuilder().buildFromRecord(record);
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

        var table = entity.getTable();
        return dslContext.insertInto(table)
                .set(changedValues)
                .returning(table.fields())
                .fetchOptional()
                .map(r -> r.into(resultType))
                .orElseThrow();
    }

    private <T> T update(Record record, Condition condition, Class<T> resultType) {
        RecordAuditor.getInstance().onUpdate(record);

        var changedValues = getChangedValues(record);

        var table = entity.getTable();
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
        for (var field : record.fields()) {
            if (record.changed(field)) {
                changedValues.put(field, record.getValue(field));
            }
        }
        return changedValues;
    }

    @Override
    public int deleteById(Object id) {
        var condition = entity.getPrimaryKeyConditionBuilder().buildFromId(id);
        return deleteByCondition(condition);
    }

    @Override
    public int deleteByCondition(Condition condition) {
        var table = entity.getTable();
        return dslContext.delete(table)
                .where(condition)
                .execute();
    }

    @Override
    public <T, F> List<T> saveDetailsAssociatedBy(TableField<?, F> foreignKey, F foreignId, Collection<T> details, Class<T> detailType) {
        var table = entity.getTable();
        var primaryKeyField = entity.getPrimaryKeyField();
        var recordFactory = new RecordFactory<T>(dslContext, detailType, table);

        List<Record> records = new ArrayList<>(details.size());
        Set<Object> ids = new HashSet<>(details.size());

        for (var detail : details) {
            var record = recordFactory.createRecord(detail);
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
