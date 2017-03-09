package cz.quantumleap.core.business;

import java.util.Map;

public interface LookupService {

    Map<Object, String> findLookupLabels(String filter);

}
