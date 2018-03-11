package cz.quantumleap.core.data;

import cz.quantumleap.core.data.mapper.MapperUtils;
import org.jooq.Record;
import org.jooq.Table;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class LookupDaoManager {

    private final ApplicationContext applicationContext;

    private Map<String, LookupDao<Table<? extends Record>>> lookupDaoMap = new HashMap<>();

    public LookupDaoManager(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public LookupDao<Table<? extends Record>> getDaoByDatabaseTableNameWithSchema(String databaseTableNameWithSchema) {
        return lookupDaoMap.get(databaseTableNameWithSchema);
    }

    @EventListener(ContextRefreshedEvent.class)
    public void initializeLookupDaoMap() {
        Map<String, LookupDao> beans = applicationContext.getBeansOfType(LookupDao.class);
        for (LookupDao lookupDao : beans.values()) {
            String tableName = MapperUtils.resolveDatabaseTableNameWithSchema(lookupDao.getTable());
            lookupDaoMap.put(tableName, lookupDao); // TODO What if table already exists?
        }
    }

    public void registerDaoForTable(Table<?> table, LookupDao lookupDao) {
        String tableName = MapperUtils.resolveDatabaseTableNameWithSchema(table);
        lookupDaoMap.put(tableName, lookupDao); // TODO What if table already exists?
    }

    @SuppressWarnings("unchecked")
    private LookupDao<Table<? extends Record>> toGenericDao(LookupDao lookupDao) {
        return (LookupDao<Table<? extends Record>>) lookupDao;
    }
}
