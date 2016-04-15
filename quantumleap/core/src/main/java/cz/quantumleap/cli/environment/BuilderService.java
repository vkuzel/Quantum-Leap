package cz.quantumleap.cli.environment;

import cz.quantumleap.common.ResourceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

@Service
public class BuilderService {

    // language=SQL
    private static final String DROP_SCHEMA_QUERY = "DROP SCHEMA public CASCADE;\n" +
            "CREATE SCHEMA public;";

    @Autowired
    private ResourceManager resourceManager;

    @Autowired
    private DataSource dataSource;

    public void buildEnvironment() {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        // TODO Sort resources by project, name!
        List<Resource> scripts = resourceManager.findOnClasspath("db/scripts/*_*.sql");
        // TODO Add support for Postgres functions and procedures.
        scripts.forEach(populator::addScript);
        populator.execute(dataSource);
    }

    public void dropEnvironment() {
        try {
            dataSource.getConnection().prepareStatement(DROP_SCHEMA_QUERY).execute();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
