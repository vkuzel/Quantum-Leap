package cz.quantumleap.core.test.common;

import org.intellij.lang.annotations.Language;
import org.jooq.DSLContext;
import org.jooq.Record;

public class TestUtils {

    public static void deleteFromTable(DSLContext dslContext, String tableName) {
        // language=SQL
        dslContext.execute("DELETE FROM " + tableName);
    }

    public static long countRowsInTable(DSLContext dslContext, String tableName) {
        // language=SQL
        Record record = dslContext.fetchOne("SELECT count(*) FROM " + tableName);
        return (long) record.get(0);
    }

    public static Object fetchFirst(DSLContext dslContext, @Language("SQL") String sql) {
        Record record = dslContext.fetchOne(sql);
        return record.get(0);
    }

    public void rollback(DSLContext dslContext) {
//        dslContext.
    }
}
