package cz.quantumleap.core.role;

import cz.quantumleap.core.data.DaoStub;
import cz.quantumleap.core.data.LookupDaoManager;
import cz.quantumleap.core.data.RecordAuditor;
import cz.quantumleap.core.tables.RoleTable;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.stereotype.Repository;

import java.util.List;

import static cz.quantumleap.core.tables.RoleTable.ROLE;

@Repository
public class RoleDao extends DaoStub<RoleTable> {

    protected RoleDao(DSLContext dslContext, LookupDaoManager lookupDaoManager, RecordAuditor recordAuditor) {
        super(ROLE, ROLE.NAME, dslContext, lookupDaoManager, recordAuditor);
    }

    public List<String> fetchRolesByPersonId(long personId) {
        // language=SQL
        String sql = "SELECT name\n" +
                "FROM core.role r\n" +
                "  JOIN core.person_role pr ON r.id = pr.role_id\n" +
                "WHERE pr.person_id = ?";
        Result<Record> records = dslContext.fetch(sql, personId);
        return records.into(String.class);
    }
}
