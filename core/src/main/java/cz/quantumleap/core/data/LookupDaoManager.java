package cz.quantumleap.core.data;

import cz.quantumleap.core.data.entity.EntityIdentifier;
import org.jooq.Record;
import org.jooq.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class LookupDaoManager {

    private static final Logger log = LoggerFactory.getLogger(LookupDaoManager.class);

    private final ApplicationContext applicationContext;

    private Map<EntityIdentifier<?>, LookupDao<Table<? extends Record>>> lookupDaoMap = new HashMap<>();

    public LookupDaoManager(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public LookupDao<Table<? extends Record>> getDaoByLookupIdentifier(EntityIdentifier<?> entityIdentifier) {
        return lookupDaoMap.get(entityIdentifier);
    }

    @EventListener(ContextRefreshedEvent.class)
    public void initializeLookupDaoMap() {
        Map<String, LookupDao> beans = applicationContext.getBeansOfType(LookupDao.class);
        for (LookupDao lookupDao : beans.values()) {
            EntityIdentifier entityIdentifier = lookupDao.getEntityIdentifier();
            if (entityIdentifier != null) {
                addDao(entityIdentifier, lookupDao);
            }
        }
    }

    public void registerDao(EntityIdentifier entityIdentifier, LookupDao lookupDao) {
        addDao(entityIdentifier, lookupDao);
    }

    @SuppressWarnings("unchecked")
    private void addDao(EntityIdentifier entityIdentifier, LookupDao lookupDao) {
        LookupDao previousDao = lookupDaoMap.get(entityIdentifier);
        if (previousDao == null) {
            lookupDaoMap.put(entityIdentifier, lookupDao);
        } else {
            log.error("Two DAOs for a table {}, existing: {}, skipped: {}", entityIdentifier, previousDao, lookupDao);
        }
    }

    @SuppressWarnings("unchecked")
    private LookupDao<Table<? extends Record>> toGenericDao(LookupDao lookupDao) {
        return (LookupDao<Table<? extends Record>>) lookupDao;
    }
}
