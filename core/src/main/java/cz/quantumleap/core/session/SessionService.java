package cz.quantumleap.core.session;

import cz.quantumleap.core.session.transport.SessionDetail;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SessionService {

    private final SessionDao sessionDao;

    public SessionService(SessionDao sessionDao) {
        this.sessionDao = sessionDao;
    }

    public List<SessionDetail> fetchListByEmail(String email) {
        return sessionDao.fetchListByEmail(email);
    }

    public void invalidate(String id) {
        sessionDao.invalidateById(id);
    }
}
