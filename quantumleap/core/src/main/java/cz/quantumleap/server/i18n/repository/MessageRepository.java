package cz.quantumleap.server.i18n.repository;

import cz.quantumleap.server.i18n.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByLanguage(String language);
}
