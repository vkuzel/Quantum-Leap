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

    private final Map<EntityIdentifier<?>, LookupDao<?>> lookupDaoMap = new HashMap<>();

    public LookupDaoManager(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @SuppressWarnings("unchecked")
    public <T extends Table<? extends Record>> LookupDao<T> getDaoByLookupIdentifier(EntityIdentifier<T> entityIdentifier) {
        return (LookupDao<T>) lookupDaoMap.get(entityIdentifier);
    }

    @EventListener(ContextRefreshedEvent.class)
    public void initializeLookupDaoMap() {
        Map<String, LookupDao> beans = applicationContext.getBeansOfType(LookupDao.class);
        for (LookupDao<?> lookupDao : beans.values()) {
            EntityIdentifier<?> entityIdentifier = lookupDao.getEntityIdentifier();
            if (entityIdentifier != null) {
                addDao(entityIdentifier, lookupDao);
            }
        }
    }

    public void registerDao(EntityIdentifier<?> entityIdentifier, LookupDao<?> lookupDao) {
        addDao(entityIdentifier, lookupDao);
    }

    private void addDao(EntityIdentifier<?> entityIdentifier, LookupDao<?> lookupDao) {
        LookupDao<?> registeredDao = lookupDaoMap.get(entityIdentifier);
        if (registeredDao == null) {
            lookupDaoMap.put(entityIdentifier, lookupDao);
        } else {
            log.error("Two DAOs exists for an entity {}, first: {}, second: {}", entityIdentifier, registeredDao, lookupDao);
        }
    }
}
