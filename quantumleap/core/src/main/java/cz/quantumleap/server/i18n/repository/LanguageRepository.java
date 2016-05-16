package cz.quantumleap.server.i18n.repository;

import cz.quantumleap.server.i18n.domain.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LanguageRepository extends JpaRepository<Language, String> {
}
