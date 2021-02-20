package cz.quantumleap.core.data;

import cz.quantumleap.core.data.entity.Entity;
import cz.quantumleap.core.data.entity.EntityIdentifier;
import org.jooq.Record;
import org.jooq.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component // TODO Rename to entity registry...
public class EntityManager {

    public static final Logger log = LoggerFactory.getLogger(EntityManager.class);

    private final ApplicationContext applicationContext;

    private final Map<EntityIdentifier<?>, Entity<?>> entityMap = new HashMap<>();

    public EntityManager(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @SuppressWarnings("unchecked")
    public <T extends Table<? extends Record>> Entity<T> getEntity(EntityIdentifier<T> entityIdentifier) {
        Entity<?> entity = entityMap.get(entityIdentifier);
        if (entity == null) {
            throw new IllegalArgumentException("Entity not found for identifier: " + entityIdentifier);
        }
        return (Entity<T>) entity;
    }

    public void registerEntity(Entity<?> entity) {
        addEntity(entity.getIdentifier(), entity);
    }

// TODO Refactor
//    @SuppressWarnings("rawtypes")
//    @EventListener(ContextRefreshedEvent.class)
//    public void initializeLookupDaoMap() {
//        Map<String, LookupDao> beans = applicationContext.getBeansOfType(LookupDao.class);
//        for (LookupDao<?> lookupDao : beans.values()) {
//            EntityIdentifier<?> entityIdentifier = lookupDao.getLookupEntityIdentifier();
//            if (entityIdentifier != null) {
//                addEntity(entityIdentifier, lookupDao.getLookupEntityIdentifier());
//            }
//        }
//    }

    private void addEntity(EntityIdentifier<?> entityIdentifier, Entity<?> entity) {
        Entity<?> originalEntity = entityMap.get(entityIdentifier);
        if (originalEntity == null) {
            entityMap.put(entityIdentifier, entity);
        } else {
            log.error("Two entities exists for entity identifier: {}, first: {}, secod: {}",
                    entityIdentifier, originalEntity, entity);
        }
    }
}
