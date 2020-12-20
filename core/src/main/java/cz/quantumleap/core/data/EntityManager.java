package cz.quantumleap.core.data;

import cz.quantumleap.core.data.entity.Entity;
import cz.quantumleap.core.data.entity.EntityIdentifier;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class EntityManager {

    private static final Logger log = LoggerFactory.getLogger(LookupDaoManager.class);

    private final ApplicationContext applicationContext;

    private final Map<EntityIdentifier<?>, Entity<?>> entityMap = new HashMap<>();

    public EntityManager(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public Entity<?> getEntityByEntityIdentifier(EntityIdentifier<?> entityIdentifier) {
        Entity<?> entity = entityMap.get(entityIdentifier);
        Validate.notNull(entity, "Entity not found for identifier: %s", entityIdentifier);
        return entity;
    }

    @SuppressWarnings("rawtypes")
    @EventListener(ContextRefreshedEvent.class)
    public void initializeEntityMap() {
        Map<String, LookupDao> beans = applicationContext.getBeansOfType(LookupDao.class);
        for (LookupDao<?> lookupDao : beans.values()) {
            EntityIdentifier<?> entityIdentifier = lookupDao.getLookupEntityIdentifier();
            Entity<?> entity = lookupDao.getLookupEntity();
            addEntity(entityIdentifier, entity);
        }
    }

    private void addEntity(EntityIdentifier<?> entityIdentifier, Entity<?> entity) {
        Validate.notNull(entityIdentifier);
        Validate.notNull(entity, "No entity for identifier: {}", entityIdentifier);
        Entity<?> registeredEntity = entityMap.get(entityIdentifier);
        if (registeredEntity == null) {
            entityMap.put(entityIdentifier, entity);
        } else {
            String msg = "Two entities exists for identifier: {}, first: {}, second: {}";
            log.error(msg, entityIdentifier, entity, registeredEntity);
        }
    }
}
