package cz.quantumleap.server.test_module.repository;

import cz.quantumleap.server.test_module.domain.TestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface TestEntityRepository extends JpaRepository<TestEntity, Long>, NativeQueryTest {
//    TestEntity findByComment(String comment);

//    @Query(value = "SELECT te.comment FROM TestEntity te")
//    List<String> getProcessedCommentsHibernate();
//
//    @Query(value = "SELECT process_text(comment) FROM test_entity", nativeQuery = true)
//    List<String> getProcessedCommentsNative();

//    @Query(value = "SELECT process_text(?)", nativeQuery = true)
//    String getProcessedString(String input);

    @Query(value = "SELECT flat_array FROM test_entity LIMIT 1", nativeQuery = true)
    List<Integer> array();

    @Query(value = "SELECT true", nativeQuery = true)
    boolean bool();

    @Query(value = "SELECT json FROM test_entity LIMIT 1", nativeQuery = true)
    Map<String, Object> jsonTest();
}
