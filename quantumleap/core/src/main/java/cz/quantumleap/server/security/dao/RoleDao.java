package cz.quantumleap.server.security.dao;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RoleDao {

    private final DSLContext dslContext;

    public RoleDao(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public List<String> findRolesByPersonId(long personId) {
        // language=SQL
        String sql = "SELECT name\n" +
                "FROM core.role r\n" +
                "  JOIN core.person_role pr ON r.id = pr.role_id\n" +
                "WHERE pr.person_id = ?";
        Result<Record> records = dslContext.fetch(sql, personId);
        return records.into(String.class);
    }
}
