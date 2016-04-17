package cz.quantumleap.server.autoincrement;

import cz.quantumleap.server.autoincrement.domain.Increment;
import cz.quantumleap.server.autoincrement.repository.IncrementRepository;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class IncrementsRunner {

    private static final Logger log = LoggerFactory.getLogger(IncrementsRunner.class);

    @Autowired
    IncrementsManager incrementsManager;

    @Autowired
    IncrementRepository incrementRepository;

    @Autowired
    DataSource dataSource;

    @PostConstruct
    // TODO Transaction
    public void runAutoIncrements() {
        Map<String, Integer> lastIncrements = incrementRepository.loadLastIncrementVersionForModules();

        List<IncrementsManager.IncrementResource> incrementResources = incrementsManager.loadAllIncrements();
        incrementResources.stream().filter(incrementResource -> {
            int lastIncrementVersion = lastIncrements.getOrDefault(incrementResource.getModuleName(), 0);
            return incrementResource.getIncrementVersion() > lastIncrementVersion;
        }).collect(Collectors.groupingBy(
                incrementResource -> Pair.of(incrementResource.getModuleName(), incrementResource.getIncrementVersion()),
                Collectors.mapping(IncrementsManager.IncrementResource::getResource, Collectors.toList())
        )).forEach((moduleVersion, resources) ->
                runOneIncrement(moduleVersion.getLeft(), moduleVersion.getRight(), resources)
        );
    }

    // TODO This should be in transaction...
    private void runOneIncrement(String moduleName, int version, List<Resource> resources) {
        log.info("Executing increment {} for module {}.", version, moduleName);

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        resources.forEach(populator::addScript);
        populator.execute(dataSource);

        resources.forEach(resource -> {
            Increment increment = new Increment(
                    moduleName,
                    version,
                    resource.getFilename()
            );
            incrementRepository.save(increment);
        });
    }
}
