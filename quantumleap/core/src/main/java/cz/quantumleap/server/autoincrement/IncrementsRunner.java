package cz.quantumleap.server.autoincrement;

import cz.quantumleap.server.autoincrement.domain.Increment;
import cz.quantumleap.server.autoincrement.repository.IncrementRepository;
import cz.quantumleap.server.common.TransactionExecutor;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class IncrementsRunner {

    private static final Logger log = LoggerFactory.getLogger(IncrementsRunner.class);

    @Autowired
    IncrementsService incrementsService;

    @Autowired
    IncrementRepository incrementRepository;

    @Autowired
    private TransactionExecutor transactionExecutor;

    @Autowired
    DataSource dataSource;

    @PostConstruct
    public void runIncrements() {
        Map<String, Integer> lastIncrements = incrementRepository.loadLastIncrementVersionForModules();
        List<IncrementsService.IncrementResource> incrementResources = incrementsService.loadAllIncrements();

        runIncrements(lastIncrements, incrementResources);
    }

    void runIncrements(Map<String, Integer> lastIncrements, List<IncrementsService.IncrementResource> incrementResources) {
        Map<Pair<String, Integer>, List<Resource>> moduleVersionResources = new LinkedHashMap<>();

        for (IncrementsService.IncrementResource incrementResource : incrementResources) {
            int lastIncrementVersion = lastIncrements.getOrDefault(incrementResource.getModuleName(), 0);
            if (incrementResource.getIncrementVersion() <= lastIncrementVersion) {
                continue;
            }

            Pair<String, Integer> moduleVersion = Pair.of(incrementResource.getModuleName(), incrementResource.getIncrementVersion());
            moduleVersionResources.compute(moduleVersion, (mv, resources) -> {
                if (resources == null) {
                    resources = new ArrayList<>();
                }

                resources.add(incrementResource.getResource());
                return resources;
            });
        }

        moduleVersionResources.forEach((moduleVersion, resources) ->
                runOneIncrement(moduleVersion.getLeft(), moduleVersion.getRight(), resources));
    }

    private void runOneIncrement(String moduleName, int version, List<Resource> resources) {
        log.info("Executing increment {} for module {}.", version, moduleName);

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        resources.forEach(populator::addScript);

        // TODO Just try to use transactional on this method ... what if it'll work inside of service?
        transactionExecutor.execute(() -> {
            populator.execute(dataSource);

            resources.forEach(resource -> {
                Increment increment = new Increment(
                        moduleName,
                        version,
                        resource.getFilename()
                );
                incrementRepository.save(increment);
            });
        });
    }
}
