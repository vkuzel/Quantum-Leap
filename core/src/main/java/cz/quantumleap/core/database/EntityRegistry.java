package cz.quantumleap.core.database;

import cz.quantumleap.core.database.entity.Entity;
import cz.quantumleap.core.database.entity.EntityIdentifier;
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

import static java.util.Objects.requireNonNull;

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

    @EventListener(ContextRefreshedEvent.class)
    public void initializeEntityMap() {
        var detailDaoMap = applicationContext.getBeansOfType(DetailDao.class);
        for (var detailDao : detailDaoMap.values()) {
            addFromDaoToMap(detailDao, DetailDao::getDetailEntity, detailEntityMap);
        }
        var listDaoMap = applicationContext.getBeansOfType(ListDao.class);
        for (var listDao : listDaoMap.values()) {
            addFromDaoToMap(listDao, ListDao::getListEntity, listEntityMap);
        }
        var lookupDaoMap = applicationContext.getBeansOfType(LookupDao.class);
        for (var lookupDao : lookupDaoMap.values()) {
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
        var entity = entityGetter.apply(dao);
        if (entity != null) {
            addToMap(entity, entityMap);
        }
    }

    private void addToMap(
            Entity<?> entity,
            Map<EntityIdentifier<?>, Entity<?>> entityMap
    ) {
        requireNonNull(entity, "Entity not specified!");

        var entityIdentifier = entity.getIdentifier();
        var originalEntity = entityMap.get(entityIdentifier);
        if (originalEntity == null) {
            entityMap.put(entityIdentifier, entity);
        } else if (originalEntity != entity) {
            var msg = "Two different entities for entity identifier {}, first: {}, second: {}";
            log.error(msg, entityIdentifier, entity, originalEntity);
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends Table<? extends Record>> Entity<T> getFromMap(
            EntityIdentifier<T> entityIdentifier,
            Map<EntityIdentifier<?>, Entity<?>> entityMap
    ) {
        var entity = entityMap.get(entityIdentifier);
        if (entity == null) {
            throw new IllegalArgumentException("Entity not found for identifier: " + entityIdentifier);
        }
        return (Entity<T>) entity;
    }
}
