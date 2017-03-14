package cz.quantumleap.core.data;

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

    private Map<Table<? extends Record>, LookupDao<Table<? extends Record>>> lookupDaoMap;

    public LookupDaoManager(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public LookupDao<Table<? extends Record>> getDaoForTable(Table<? extends Record> table) {
        return lookupDaoMap.get(table);
    }

    @PostConstruct
    public void initializeLookupDaoMap() {
        Map<String, LookupDao> beans = applicationContext.getBeansOfType(LookupDao.class);
        lookupDaoMap = beans.values().stream().collect(Collectors.toMap(LookupDao::getTable, this::toGenericDao));
    }

    @SuppressWarnings("unchecked")
    private LookupDao<Table<? extends Record>> toGenericDao(LookupDao lookupDao) {
        return (LookupDao<Table<? extends Record>>) lookupDao;
    }
}
