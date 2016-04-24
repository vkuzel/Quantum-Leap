package cz.quantumleap.server.autoincrement;

import cz.quantumleap.server.autoincrement.domain.Increment;
import cz.quantumleap.server.autoincrement.repository.IncrementRepository;
import cz.quantumleap.server.common.Utils;
import cz.quantumleap.server.persistence.TransactionExecutor;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class IncrementsRunner {

    private static final Logger log = LoggerFactory.getLogger(IncrementsRunner.class);

    @Autowired
    private IncrementsService incrementsService;

    @Autowired
    private IncrementRepository incrementRepository;

    @Autowired
    private TransactionExecutor transactionExecutor;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void runIncrements() {
        Map<String, Integer> lastIncrements = incrementRepository.loadLastIncrementVersionForModules();
        List<IncrementsService.IncrementScript> incrementScripts = incrementsService.loadAllIncrements();

        runIncrements(lastIncrements, incrementScripts);
    }

    void runIncrements(Map<String, Integer> lastIncrements, List<IncrementsService.IncrementScript> incrementScripts) {
        Map<Pair<String, Integer>, List<Resource>> moduleVersionScripts = new LinkedHashMap<>();

        for (IncrementsService.IncrementScript incrementScript : incrementScripts) {
            int lastIncrementVersion = lastIncrements.getOrDefault(incrementScript.getModuleName(), 0);
            if (incrementScript.getIncrementVersion() <= lastIncrementVersion) {
                continue;
            }

            Pair<String, Integer> moduleVersion = Pair.of(incrementScript.getModuleName(), incrementScript.getIncrementVersion());
            moduleVersionScripts.compute(moduleVersion, (mv, scripts) -> {
                if (scripts == null) {
                    scripts = new ArrayList<>();
                }

                scripts.add(incrementScript.getScript());
                return scripts;
            });
        }

        moduleVersionScripts.forEach((moduleVersion, scripts) ->
                runOneIncrement(moduleVersion.getLeft(), moduleVersion.getRight(), scripts));
    }

    private void runOneIncrement(String moduleName, int version, List<Resource> scripts) {
        log.info("Executing increment {} for module {}.", version, moduleName);

        transactionExecutor.execute(() ->
                scripts.forEach(script -> {
                    String sql = Utils.resourceToString(script);
                    jdbcTemplate.execute(sql);

                    Increment increment = new Increment(
                            moduleName,
                            version,
                            script.getFilename()
                    );
                    incrementRepository.save(increment);
                }));
    }
}
