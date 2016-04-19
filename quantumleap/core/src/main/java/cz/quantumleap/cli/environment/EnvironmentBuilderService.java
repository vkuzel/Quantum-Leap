package cz.quantumleap.cli.environment;

import cz.quantumleap.server.autoincrement.IncrementsService;
import cz.quantumleap.server.autoincrement.repository.IncrementRepositoryImpl;
import cz.quantumleap.server.common.ModuleDependencyManager;
import cz.quantumleap.server.common.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EnvironmentBuilderService {

    private static final Logger log = LoggerFactory.getLogger(EnvironmentBuilderService.class);

    // TODO ProjectDependencyGraph for tests...
    private static final String SCRIPTS_LOCATION_PATTERN = "db/scripts/*_*.sql";

    // language=SQL
    private static final String DROP_SCHEMA_QUERY = "DROP SCHEMA public CASCADE;\n" +
            "CREATE SCHEMA public;";

    @Autowired
    ModuleDependencyManager moduleDependencyManager;

    @Autowired
    private ResourceManager resourceManager;

    @Autowired
    private IncrementsService incrementsService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    public void buildEnvironment() {
        log.info("Building new environment.");
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        List<ResourceManager.ModuleResource> scripts = resourceManager.findOnClasspath(SCRIPTS_LOCATION_PATTERN);
        // TODO Add support for Postgres functions and procedures.
        scripts.forEach(script -> populator.addScript(script.getResource()));
        populator.execute(jdbcTemplate.getDataSource());

        incrementsService.getLatestIncrementVersionForModules().forEach((moduleName, version) ->
                IncrementRepositoryImpl.insertEmptyIncrement(jdbcTemplate.getDataSource(), moduleName, version));
    }

    @Transactional
    public void dropEnvironment() {
        log.info("Dropping existing environment.");
        jdbcTemplate.execute(DROP_SCHEMA_QUERY);
    }
}
