package cz.quantumleap.core.autoincrement;

import cz.quantumleap.core.database.DefaultDetailDao;
import cz.quantumleap.core.database.DetailDao;
import cz.quantumleap.core.database.RecordAuditor;
import cz.quantumleap.core.database.entity.Entity;
import cz.quantumleap.core.tables.IncrementTable;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.TableField;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static cz.quantumleap.core.tables.IncrementTable.INCREMENT;

@Repository
public class IncrementDao implements DetailDao<IncrementTable> {

    private final Entity<IncrementTable> entity;
    private final DSLContext dslContext;
    private final DetailDao<IncrementTable> detailDao;

    public IncrementDao(DSLContext dslContext, RecordAuditor recordAuditor) {
        this.dslContext = dslContext;
        this.entity = createEntity();
        this.detailDao = DefaultDetailDao.builder(entity, dslContext, recordAuditor).build();
    }

    private Entity<IncrementTable> createEntity() {
        return Entity.builder(INCREMENT).build();
    }

    @Override
    public Entity<IncrementTable> getDetailEntity() {
        return entity;
    }

    public Map<String, Integer> loadLastIncrementVersionForModules() {
        return dslContext
                .select(INCREMENT.MODULE, DSL.max(INCREMENT.VERSION))
                .from(INCREMENT)
                .groupBy(INCREMENT.MODULE)
                .fetchMap(Record2::value1, Record2::value2);
    }

    @Override
    public <T> T fetchById(Object id, Class<T> type) {
        return detailDao.fetchById(id, type);
    }

    @Override
    public <T> T fetchByCondition(Condition condition, Class<T> type) {
        return detailDao.fetchByCondition(condition, type);
    }

    @Transactional
    @Override
    public <T> T save(T detail) {
        return detailDao.save(detail);
    }

    @Override
    public <T> List<T> saveAll(List<T> details) {
        return detailDao.saveAll(details);
    }

    @Override
    public int deleteById(Object id) {
        return detailDao.deleteById(id);
    }

    @Override
    public int deleteByCondition(Condition condition) {
        return detailDao.deleteByCondition(condition);
    }

    @Override
    public <T, F> List<T> saveDetailsAssociatedBy(TableField<?, F> foreignKey, F foreignId, Collection<T> details, Class<T> detailType) {
        return detailDao.saveDetailsAssociatedBy(foreignKey, foreignId, details, detailType);
    }
}
