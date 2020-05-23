package cz.quantumleap.core.business;

import cz.quantumleap.core.data.entity.EntityIdentifier;

import java.util.Map;

public interface LookupService extends ListService {

    EntityIdentifier<?> getLookupEntityIdentifier();

    String findLookupLabel(Object id);

    Map<Object, String> findLookupLabels(String query);
}
