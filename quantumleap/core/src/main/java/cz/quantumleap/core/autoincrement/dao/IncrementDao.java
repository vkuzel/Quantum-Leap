package cz.quantumleap.core.autoincrement.dao;

import cz.quantumleap.core.tables.records.IncrementRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Map;

import static cz.quantumleap.core.tables.Increment.INCREMENT;

@Repository
public class IncrementDao {

    private final DSLContext dslContext;

    IncrementDao(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public Map<String, Integer> loadLastIncrementVersionForModules() {
        String query = "SELECT module, MAX(version) AS version FROM core.increment GROUP BY module";
        Result<Record> records = dslContext.fetch(query);
        return records.intoMap(INCREMENT.MODULE, INCREMENT.VERSION);
    }

    public void createIncrement(String module, int version, String fileName) {
        IncrementRecord increment = dslContext.newRecord(INCREMENT);
        increment.setModule(module);
        increment.setVersion(version);
        increment.setFileName(fileName);
        increment.store();
    }
}
