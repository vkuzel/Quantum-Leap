package cz.quantumleap.server.autoincrement.repository;

import java.util.Map;

public interface LatestIncrementsLoader {

    Map<String, Integer> loadLastIncrementVersionForProjects();

}
