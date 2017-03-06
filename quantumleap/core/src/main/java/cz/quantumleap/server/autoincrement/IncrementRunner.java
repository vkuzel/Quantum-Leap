package cz.quantumleap.server.autoincrement;

import cz.quantumleap.core.autoincrement.dao.IncrementDao;
import cz.quantumleap.core.autoincrement.IncrementService;
import cz.quantumleap.core.autoincrement.transport.Increment;
import cz.quantumleap.core.common.Utils;
import cz.quantumleap.core.persistence.TransactionExecutor;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class IncrementRunner {

    private static final Logger log = LoggerFactory.getLogger(IncrementRunner.class);

    private final Environment environment;
    private final IncrementService incrementService;
    private final TransactionExecutor transactionExecutor;
    private final IncrementDao incrementDao;

    IncrementRunner(
            Environment environment,
            IncrementService incrementService,
            TransactionExecutor transactionExecutor,
            IncrementDao incrementDao
    ) {
        this.environment = environment;
        this.incrementService = incrementService;
        this.transactionExecutor = transactionExecutor;
        this.incrementDao = incrementDao;
    }

    @PostConstruct
    public void runIncrements() {
        if (!environment.acceptsProfiles("test")) {
            Map<String, Integer> lastIncrements = incrementDao.loadLastIncrementVersionForModules();
            List<IncrementService.IncrementScript> incrementScripts = incrementService.findAllIncrementsInClasspath();

            runIncrements(lastIncrements, incrementScripts);
        }
    }

    void runIncrements(Map<String, Integer> lastIncrements, List<IncrementService.IncrementScript> incrementScripts) {
        Map<Pair<String, Integer>, List<Resource>> moduleVersionScripts = new LinkedHashMap<>();

        for (IncrementService.IncrementScript incrementScript : incrementScripts) {
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

        transactionExecutor.execute(dslContext -> {
            scripts.forEach(script -> {
                String sql = Utils.loadResourceToString(script);
                dslContext.execute(sql);
                incrementDao.save(createIncrement(moduleName, version, script.getFilename()));
            });
        });
    }

    private Increment createIncrement(String module, int version, String fileName) {
        Increment increment = new Increment();
        increment.setModule(module);
        increment.setVersion(version);
        increment.setFileName(fileName);
        return increment;
    }
}
