package cz.quantumleap.core.session;

import cz.quantumleap.core.security.AuthenticationEmailResolver;
import cz.quantumleap.core.session.transport.SessionDetail;
import org.apache.commons.lang3.Validate;
import org.springframework.boot.autoconfigure.session.SessionProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.session.MapSession;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.*;

@Repository
@EnableConfigurationProperties(SessionProperties.class)
public class SessionDao implements SessionRepository<MapSession> {

    public static final String EMAIL_ATTRIBUTE = "EMAIL";
    public static final String REMOTE_ADDRESS_ATTRIBUTE = "REMOTE_ADDRESS";
    public static final String USER_AGENT_ATTRIBUTE = "USER_AGENT";

    private final Map<String, Session> sessionMap;
    private final MapSessionRepository repository;
    private final AuthenticationEmailResolver authenticationEmailResolver = new AuthenticationEmailResolver();

    public SessionDao(SessionProperties sessionProperties) {
        this.sessionMap = new HashMap<>();
        this.repository = new MapSessionRepository(sessionMap);
        if (sessionProperties.getTimeout() != null) {
            this.repository.setDefaultMaxInactiveInterval((int) sessionProperties.getTimeout().getSeconds());
        }
    }

    @Override
    public MapSession createSession() {
        MapSession mapSession = repository.createSession();
        addEmailToSession(mapSession);
        return mapSession;
    }

    @Override
    public void save(MapSession session) {
        addEmailToSession(session);
        repository.save(session);
    }

    @Override
    public MapSession findById(String id) {
        return repository.findById(id);
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    public void invalidateById(String id) {
        Session session = sessionMap.get(id);
        session.setMaxInactiveInterval(Duration.ZERO);
    }

    @Scheduled(initialDelay = 3600, fixedRate = 3600)
    public void deleteExpiredSessions() {
        List<Session> sessions = new ArrayList<>(sessionMap.values());
        for (Session session : sessions) {
            if (session.isExpired()) {
                repository.deleteById(session.getId());
            }
        }
    }

    public List<SessionDetail> fetchListByEmail(String email) {
        Validate.notNull(email);
        List<SessionDetail> sessions = new ArrayList<>();
        for (Session session : sessionMap.values()) {
            if (email.equals(session.getAttribute(EMAIL_ATTRIBUTE))) {
                sessions.add(new SessionDetail(session));
            }
        }
        sessions.sort(Comparator.comparing(SessionDetail::getCreatedAt));
        return sessions;
    }

    private void addEmailToSession(Session session) {
        if (session.getAttribute(EMAIL_ATTRIBUTE) != null) {
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return;
        }

        String email = authenticationEmailResolver.resolve(authentication);
        session.setAttribute(EMAIL_ATTRIBUTE, email);
    }
}
