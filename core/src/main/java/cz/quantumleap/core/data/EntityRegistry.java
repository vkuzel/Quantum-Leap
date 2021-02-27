package cz.quantumleap.core.data;

import cz.quantumleap.core.data.entity.Entity;
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
import java.util.function.Function;

@Component
public class EntityRegistry {

    public static final Logger log = LoggerFactory.getLogger(EntityRegistry.class);

    private final ApplicationContext applicationContext;

    private final Map<EntityIdentifier<?>, Entity<?>> entityMap = new HashMap<>();

    public EntityRegistry(ApplicationContext applicationContext) {
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

    @SuppressWarnings("rawtypes")
    @EventListener(ContextRefreshedEvent.class)
    public void initializeEntityMap() {
        Map<String, DetailDao> detailDaoMap = applicationContext.getBeansOfType(DetailDao.class);
        for (DetailDao detailDao : detailDaoMap.values()) {
            addEntity(detailDao, DetailDao::getDetailEntityIdentifier, DetailDao::getDetailEntity);
        }
        Map<String, ListDao> listDaoMap = applicationContext.getBeansOfType(ListDao.class);
        for (ListDao listDao : listDaoMap.values()) {
            addEntity(listDao, ListDao::getListEntityIdentifier, ListDao::getListEntity);
        }
        Map<String, LookupDao> lookupDaoMap = applicationContext.getBeansOfType(LookupDao.class);
        for (LookupDao lookupDao : lookupDaoMap.values()) {
            addEntity(lookupDao, LookupDao::getLookupEntityIdentifier, LookupDao::getLookupEntity);
        }
    }

    private <DAO> void addEntity(
            DAO dao,
            Function<DAO, EntityIdentifier<?>> entityIdentifierGetter,
            Function<DAO, Entity<?>> entityGetter
    ) {
        EntityIdentifier<?> entityIdentifier = entityIdentifierGetter.apply(dao);
        Entity<?> entity = entityGetter.apply(dao);
        if (entityIdentifier == null || entity == null) {
            return;
        }

        Entity<?> originalEntity = entityMap.put(entityIdentifier, entity);
        if (originalEntity != null && originalEntity != entity) {
            String msg = "Two different entities for entity identifier %s, first: %s, second: %s";
            throw new IllegalStateException(String.format(msg, entityIdentifier, entity, originalEntity));
        }
    }
}
