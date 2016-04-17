package cz.quantumleap.server.autoincrement.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IncrementRepositoryImpl implements LatestIncrementsLoader {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Integer> loadLastIncrementVersionForModules() {
        String query = "SELECT module, MAX(version) AS version FROM increment GROUP BY module";
        List<Object[]> resultList = entityManager.createNativeQuery(query).getResultList();
        return resultList.stream().collect(Collectors.toMap(
                row -> (String) row[0],
                row -> (Integer) row[1]
        ));
    }

    public static void insertEmptyIncrement(DataSource dataSource, String module, int version) {
        // language=SQL
        String query = "INSERT INTO increment (module, version, file_name, created_at) " +
                "VALUES (?, ?, '<initial_increment>', NOW())"; // TODO Fix created_at column! Maybe create Hibernate's pre-commit hook for createdAt and createdBy, etc...
        try { // TODO JdbcTemplate for this? Maybe not, there are just few cases of calling jdbc directly. Right?
            PreparedStatement statement = dataSource.getConnection().prepareStatement(query);
            statement.setString(1, module);
            statement.setInt(2, version);
            statement.execute();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
