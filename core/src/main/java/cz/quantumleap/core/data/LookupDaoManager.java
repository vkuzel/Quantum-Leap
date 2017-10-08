package cz.quantumleap.core.data;

import cz.quantumleap.core.data.mapper.MapperUtils;
import org.jooq.Record;
import org.jooq.Table;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class LookupDaoManager {

    private final ApplicationContext applicationContext;

    private Map<String, LookupDao<Table<? extends Record>>> lookupDaoMap;

    public LookupDaoManager(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public LookupDao<Table<? extends Record>> getDaoByDatabaseTableNameWithSchema(String databaseTableNameWithSchema) {
        return lookupDaoMap.get(databaseTableNameWithSchema);
    }

    @PostConstruct
    public void initializeLookupDaoMap() {
        Map<String, LookupDao> beans = applicationContext.getBeansOfType(LookupDao.class);
        lookupDaoMap = beans.values().stream().collect(Collectors.toMap(dao -> MapperUtils.resolveDatabaseTableNameWithSchema(dao.getTable()), this::toGenericDao));
    }

    @SuppressWarnings("unchecked")
    private LookupDao<Table<? extends Record>> toGenericDao(LookupDao lookupDao) {
        return (LookupDao<Table<? extends Record>>) lookupDao;
    }
}
