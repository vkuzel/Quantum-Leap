package cz.quantumleap.core.autoincrement;

import cz.quantumleap.core.data.*;
import cz.quantumleap.core.data.detail.PrimaryKeyConditionBuilder;
import cz.quantumleap.core.tables.IncrementTable;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record2;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

import static cz.quantumleap.core.tables.IncrementTable.INCREMENT;

@Repository
public class IncrementDao implements DetailDao<IncrementTable> {

    private final DSLContext dslContext;
    private final DetailDao<IncrementTable> detailDao;

    public IncrementDao(DSLContext dslContext, LookupDaoManager lookupDaoManager, RecordAuditor recordAuditor) {
        this.dslContext = dslContext;
        PrimaryKeyConditionBuilder primaryKeyConditionBuilder = new PrimaryKeyConditionBuilder(INCREMENT);
        MapperFactory mapperFactory = new MapperFactory(INCREMENT, lookupDaoManager);
        this.detailDao = new DefaultDetailDao<>(INCREMENT, dslContext, primaryKeyConditionBuilder, mapperFactory, recordAuditor);
    }

    public Map<String, Integer> loadLastIncrementVersionForModules() {
        return dslContext
                .select(INCREMENT.MODULE, INCREMENT.VERSION.max())
                .from(INCREMENT)
                .groupBy(INCREMENT.MODULE)
                .fetchMap(Record2::value1, Record2::value2);
    }

    @Override
    public <T> Optional<T> fetchById(Object id, Class<T> type) {
        return detailDao.fetchById(id, type);
    }

    @Override
    public <T> Optional<T> fetchByCondition(Condition condition, Class<T> type) {
        return detailDao.fetchByCondition(condition, type);
    }

    @Transactional
    @Override
    public <T> T save(T detail) {
        return detailDao.save(detail);
    }

    @Override
    public void deleteById(Object id) {
        detailDao.deleteById(id);
    }
}
