package cz.quantumleap.core.business;

import java.util.Map;

public interface LookupService extends ListService {

    String findLookupLabel(Object id);

    Map<Object, String> findLookupLabels(String filter);
}
