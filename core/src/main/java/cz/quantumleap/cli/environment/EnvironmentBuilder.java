package cz.quantumleap.cli.environment;

import cz.quantumleap.core.autoincrement.IncrementDao;
import cz.quantumleap.core.autoincrement.IncrementService;
import cz.quantumleap.core.autoincrement.transport.Increment;
import cz.quantumleap.core.common.Utils;
import cz.quantumleap.core.module.ModuleDependencyManager;
import cz.quantumleap.core.resource.ResourceManager;
import cz.quantumleap.core.resource.ResourceWithModule;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

        List<ResourceWithModule> scripts = resourceManager.findInClasspath(SCRIPTS_LOCATION_PATTERN);
        scripts.forEach(script -> {
            log.debug("Executing script {}", script.getResourcePath());
            String sql = Utils.readResourceToString(script.getResource());
            dslContext.execute(sql);
        });

        incrementService.getLatestIncrementVersionForModules().forEach(
                (moduleName, version) -> incrementDao.save(createIncrement(moduleName, version))
        );
    }

    private Increment createIncrement(String module, int version) {
        Increment increment = new Increment();
        increment.setModule(module);
        increment.setVersion(version);
        increment.setFileName("<initial_increment>");
        return increment;
    }

    @Transactional
    public void dropEnvironment() {
        log.info("Dropping existing environment.");

        for (String moduleName : moduleDependencyManager.getModuleNames()) {
            String schemaName = moduleName.replace('-', '_');
            log.debug("Dropping schema {}", schemaName);
            environmentDao.dropSchema(schemaName);
        }
    }
}
