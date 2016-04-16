package cz.quantumleap.server.autoincrement.repository;

import cz.quantumleap.server.autoincrement.domain.Increment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncrementRepository extends JpaRepository<Increment, Long>, LatestIncrementsLoader {

}
