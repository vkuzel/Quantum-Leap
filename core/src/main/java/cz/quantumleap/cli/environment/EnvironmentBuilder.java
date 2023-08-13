package cz.quantumleap.cli.environment;

import cz.quantumleap.core.autoincrement.IncrementDao;
import cz.quantumleap.core.autoincrement.IncrementService;
import cz.quantumleap.core.autoincrement.domain.Increment;
import cz.quantumleap.core.common.Utils;
import cz.quantumleap.core.module.ModuleDependencyManager;
import cz.quantumleap.core.resource.ResourceManager;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EnvironmentBuilder {

    private static final Logger log = LoggerFactory.getLogger(EnvironmentBuilder.class);

    private static final String SCRIPTS_LOCATION_PATTERN = "db/scripts/*_*.sql";

    private final ResourceManager resourceManager;
    private final IncrementService incrementService;
    private final ModuleDependencyManager moduleDependencyManager;
    private final DSLContext dslContext;
    private final IncrementDao incrementDao;
    private final EnvironmentDao environmentDao;

    public EnvironmentBuilder(
            ResourceManager resourceManager,
            IncrementService incrementService,
            ModuleDependencyManager moduleDependencyManager,
            DSLContext dslContext,
            IncrementDao incrementDao,
            EnvironmentDao environmentDao
    ) {
        this.resourceManager = resourceManager;
        this.incrementService = incrementService;
        this.moduleDependencyManager = moduleDependencyManager;
        this.dslContext = dslContext;
        this.incrementDao = incrementDao;
        this.environmentDao = environmentDao;
    }

    @Transactional
    public void buildEnvironment() {
        log.info("Building new environment.");

        var scripts = resourceManager.findInClasspath(SCRIPTS_LOCATION_PATTERN);
        scripts.forEach(script -> {
            log.info("Executing script {}", script.getResourcePath());
            var sql = Utils.readResourceToString(script.getResource());
            dslContext.execute(sql);
        });

        incrementService.getLatestIncrementVersionForModules().forEach(
                (moduleName, version) -> incrementDao.save(createIncrement(moduleName, version))
        );
    }

    private Increment createIncrement(String module, int version) {
        var increment = new Increment();
        increment.setModule(module);
        increment.setVersion(version);
        increment.setFileName("<initial_increment>");
        return increment;
    }

    @Transactional
    public void dropEnvironment() {
        log.info("Dropping existing environment.");

        for (var moduleName : moduleDependencyManager.getModuleNames()) {
            var schemaName = moduleName.replace('-', '_');
            log.info("Dropping schema {}", schemaName);
            environmentDao.dropSchema(schemaName);
        }
    }
}
