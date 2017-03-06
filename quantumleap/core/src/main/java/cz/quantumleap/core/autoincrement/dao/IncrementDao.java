package cz.quantumleap.core.autoincrement.dao;

import cz.quantumleap.core.persistence.dao.DefaultCrudDao;
import cz.quantumleap.core.persistence.dao.lookup.LookupDaoManager;
import cz.quantumleap.core.persistence.dao.MapperFactory;
import org.jooq.DSLContext;
import org.jooq.Record2;
import org.springframework.stereotype.Repository;

import java.util.Map;

import static cz.quantumleap.core.tables.IncrementTable.INCREMENT;

@Repository
public class IncrementDao extends DefaultCrudDao {

    protected IncrementDao(DSLContext dslContext, LookupDaoManager lookupDaoManager) {
        super(INCREMENT, dslContext, new MapperFactory(INCREMENT, lookupDaoManager));
    }

    public Map<String, Integer> loadLastIncrementVersionForModules() {
        return dslContext
                .select(INCREMENT.MODULE, INCREMENT.VERSION.max())
                .from(INCREMENT)
                .groupBy(INCREMENT.MODULE)
                .fetchMap(Record2::value1, Record2::value2);
    }
}
