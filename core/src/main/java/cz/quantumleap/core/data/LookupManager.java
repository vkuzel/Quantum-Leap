package cz.quantumleap.core.data;

import cz.quantumleap.core.data.entity.EntityIdentifier;
import org.springframework.stereotype.Component;

@Component
public class LookupManager {

    private final LookupDaoManager lookupDaoManager;

    public LookupManager(LookupDaoManager lookupDaoManager) {
        this.lookupDaoManager = lookupDaoManager;
    }

    public String getLabel(String entityIdentifier, Object id) {
        EntityIdentifier<?> identifier = EntityIdentifier.parse(entityIdentifier);
        return getLabel(identifier, id);
    }

    public String getLabel(EntityIdentifier<?> entityIdentifier, Object id) {
        if (id == null) {
            return null;
        }

        LookupDao<?> lookupDao = lookupDaoManager.getDaoByEntityIdentifier(entityIdentifier);
        if (lookupDao == null) {
            String msgPattern = "Lookup dao not found for entity identifier %s";
            throw new IllegalStateException(String.format(msgPattern, entityIdentifier));
        }

        return lookupDao.fetchLabelById(id);
    }
}
