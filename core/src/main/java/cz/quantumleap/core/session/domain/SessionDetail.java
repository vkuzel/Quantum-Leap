package cz.quantumleap.core.session.domain;

import cz.quantumleap.core.session.SessionDao;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.session.Session;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class SessionDetail {

    private final Session session;
    private final UserAgent userAgent;

    public SessionDetail(Session session) {
        this.session = session;
        this.userAgent = UserAgent.parseUserAgentString(session.getAttribute(SessionDao.USER_AGENT_ATTRIBUTE));
    }

    public String getId() {
        return session.getId();
    }

    public LocalDateTime getCreatedAt() {
        return LocalDateTime.ofInstant(session.getCreationTime(), ZoneId.systemDefault());
    }

    public LocalDateTime getLastAccessAt() {
        return LocalDateTime.ofInstant(session.getLastAccessedTime(), ZoneId.systemDefault());
    }

    public LocalDateTime getExpireAt() {
        Instant expirationTime = session.getLastAccessedTime().plus(session.getMaxInactiveInterval());
        return LocalDateTime.ofInstant(expirationTime, ZoneId.systemDefault());
    }

    public boolean isExpired() {
        return session.isExpired();
    }

    public String getRemoteAddress() {
        return session.getAttribute(SessionDao.REMOTE_ADDRESS_ATTRIBUTE);
    }

    public String getUserAgentBrowser() {
        return userAgent.getBrowser().getName();
    }

    public String getUserAgentOperatingSystem() {
        return userAgent.getOperatingSystem().getName();
    }

    public String getUserAgentDevice() {
        return userAgent.getOperatingSystem().getDeviceType().getName();
    }
}
