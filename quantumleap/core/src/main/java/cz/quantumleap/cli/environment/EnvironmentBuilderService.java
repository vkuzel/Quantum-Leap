package cz.quantumleap.cli.environment;

import cz.quantumleap.server.autoincrement.IncrementsService;
import cz.quantumleap.server.autoincrement.repository.IncrementRepositoryImpl;
import cz.quantumleap.server.common.ModuleDependencyManager;
import cz.quantumleap.server.common.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

@Service
public class EnvironmentBuilderService {

    private static final Logger log = LoggerFactory.getLogger(EnvironmentBuilderService.class);
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
    private DataSource dataSource;

    public void buildEnvironment() {
        log.info("Building new environment.");
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        List<ResourceManager.ModuleResource> scripts = resourceManager.findOnClasspath(SCRIPTS_LOCATION_PATTERN);
        // TODO Add support for Postgres functions and procedures.
        scripts.forEach(script -> populator.addScript(script.getResource()));
        populator.execute(dataSource);

        incrementsService.getLatestIncrementVersionForModules().forEach((moduleName, version) ->
                IncrementRepositoryImpl.insertEmptyIncrement(dataSource, moduleName, version));
    }

    public void dropEnvironment() {
        log.info("Dropping existing environment.");
        try {
            dataSource.getConnection().prepareStatement(DROP_SCHEMA_QUERY).execute();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
