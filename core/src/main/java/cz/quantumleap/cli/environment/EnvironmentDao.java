package cz.quantumleap.cli.environment;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
public class EnvironmentDao {

    private final DSLContext dslContext;

    EnvironmentDao(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public void dropSchema(String schemaName) {
        // language=SQL
        var sql = "DROP SCHEMA IF EXISTS " + schemaName + " CASCADE";
        dslContext.execute(sql);
    }
}
