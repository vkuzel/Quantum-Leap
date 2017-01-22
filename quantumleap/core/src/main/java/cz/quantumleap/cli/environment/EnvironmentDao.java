package cz.quantumleap.cli.environment;

import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentDao {

    private final DSLContext dslContext;

    EnvironmentDao(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public void dropSchema(String schemaName) {
        // language=SQL
        String sql = "DROP SCHEMA IF EXISTS " + schemaName + " CASCADE";
        dslContext.execute(sql);
    }
}
