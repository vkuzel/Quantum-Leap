package cz.quantumleap.cli.environment.dao;

import org.jooq.DSLContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
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
