package cz.quantumleap.core.business;

import java.util.Map;

public interface LookupService extends ListService {

    Map<Object, String> findLookupLabels(String filter);

}
