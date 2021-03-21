package cz.quantumleap.core.database;

import cz.quantumleap.core.database.entity.Entity;
import cz.quantumleap.core.database.entity.EntityIdentifier;
import org.apache.commons.lang3.Validate;
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
import java.util.function.Function;

@Component
public class EntityRegistry {

    public static final Logger log = LoggerFactory.getLogger(EntityRegistry.class);

    private final ApplicationContext applicationContext;

    private final Map<EntityIdentifier<?>, Entity<?>> detailEntityMap = new HashMap<>();
    private final Map<EntityIdentifier<?>, Entity<?>> listEntityMap = new HashMap<>();
    private final Map<EntityIdentifier<?>, Entity<?>> lookupEntityMap = new HashMap<>();

    public EntityRegistry(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public <T extends Table<? extends Record>> Entity<T> getLookupEntity(EntityIdentifier<T> entityIdentifier) {
        return getFromMap(entityIdentifier, lookupEntityMap);
    }

    @SuppressWarnings("rawtypes")
    @EventListener(ContextRefreshedEvent.class)
    public void initializeEntityMap() {
        Map<String, DetailDao> detailDaoMap = applicationContext.getBeansOfType(DetailDao.class);
        for (DetailDao detailDao : detailDaoMap.values()) {
            addFromDaoToMap(detailDao, DetailDao::getDetailEntity, detailEntityMap);
        }
        Map<String, ListDao> listDaoMap = applicationContext.getBeansOfType(ListDao.class);
        for (ListDao listDao : listDaoMap.values()) {
            addFromDaoToMap(listDao, ListDao::getListEntity, listEntityMap);
        }
        Map<String, LookupDao> lookupDaoMap = applicationContext.getBeansOfType(LookupDao.class);
        for (LookupDao lookupDao : lookupDaoMap.values()) {
            addFromDaoToMap(lookupDao, LookupDao::getLookupEntity, lookupEntityMap);
        }
    }

    public void addLookupEntity(Entity<?> entity) {
        addToMap(entity, lookupEntityMap);
    }

    private <DAO> void addFromDaoToMap(
            DAO dao,
            Function<DAO, Entity<?>> entityGetter,
            Map<EntityIdentifier<?>, Entity<?>> entityMap
    ) {
        Entity<?> entity = entityGetter.apply(dao);
        if (entity != null) {
            addToMap(entity, entityMap);
        }
    }

    private void addToMap(
            Entity<?> entity,
            Map<EntityIdentifier<?>, Entity<?>> entityMap
    ) {
        Validate.notNull(entity, "Entity not specified!");

        EntityIdentifier<?> entityIdentifier = entity.getIdentifier();
        Entity<?> originalEntity = entityMap.get(entityIdentifier);
        if (originalEntity == null) {
            entityMap.put(entityIdentifier, entity);
        } else if (originalEntity != entity) {
            String msg = "Two different entities for entity identifier {}, first: {}, second: {}";
            log.error(msg, entityIdentifier, entity, originalEntity);
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends Table<? extends Record>> Entity<T> getFromMap(
            EntityIdentifier<T> entityIdentifier,
            Map<EntityIdentifier<?>, Entity<?>> entityMap
    ) {
        Entity<?> entity = entityMap.get(entityIdentifier);
        if (entity == null) {
            throw new IllegalArgumentException("Entity not found for identifier: " + entityIdentifier);
        }
        return (Entity<T>) entity;
    }
}
