package cz.quantumleap.core.persistence.lookup;

import cz.quantumleap.core.persistence.LookupDao;
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

    private Map<Table<? extends Record>, LookupDao> lookupDaoMap;

    public LookupDaoManager(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public LookupDao getDaoForTable(Table<Record> table) {
        return lookupDaoMap.get(table);
    }

    @PostConstruct
    public void initializeLookupDaoMap() {
        Map<String, LookupDao> beans = applicationContext.getBeansOfType(LookupDao.class);
        lookupDaoMap = beans.values().stream().collect(Collectors.toMap(LookupDao::getTable, bean -> bean));
    }
}
