package cz.quantumleap.test_module.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class TestEntityRepositoryImpl implements NativeQueryTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public int countTestEnties() {
        String query = "SELECT CAST(COUNT(*) AS INT) AS count FROM test_entity";
        return (int) entityManager.createNativeQuery(query).getSingleResult();
    }
}
