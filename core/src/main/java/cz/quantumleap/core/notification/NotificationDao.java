package cz.quantumleap.core.notification;

import cz.quantumleap.core.data.DaoStub;
import cz.quantumleap.core.data.EnumManager;
import cz.quantumleap.core.data.LookupDaoManager;
import cz.quantumleap.core.data.RecordAuditor;
import cz.quantumleap.core.data.entity.Entity;
import cz.quantumleap.core.data.transport.Lookup;
import cz.quantumleap.core.data.transport.Slice;
import cz.quantumleap.core.data.transport.SliceRequest;
import cz.quantumleap.core.data.transport.Table;
import cz.quantumleap.core.notification.transport.Notification;
import cz.quantumleap.core.tables.NotificationTable;
import cz.quantumleap.core.tables.PersonTable;
import cz.quantumleap.core.tables.RoleTable;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static cz.quantumleap.core.tables.NotificationTable.NOTIFICATION;
import static cz.quantumleap.core.tables.PersonRoleTable.PERSON_ROLE;

@Repository
public class NotificationDao extends DaoStub<NotificationTable> {

    private static final int MAX_UNRESOLVED_NOTIFICATIONS_TO_SHOW = 10;

    protected NotificationDao(DSLContext dslContext, LookupDaoManager lookupDaoManager, EnumManager enumManager, RecordAuditor recordAuditor) {
        super(createEntity(), dslContext, lookupDaoManager, enumManager, recordAuditor);
    }

    private static Entity<NotificationTable> createEntity() {
        return Entity.createBuilder(NOTIFICATION).build();
    }

    public Notification createNotificationForPerson(String notificationCode, List<String> messageArguments, long personId) {
        Condition condition = createUnresolvedNotificationCondition(notificationCode, messageArguments)
                .and(NOTIFICATION.PERSON_ID.eq(personId));
        Notification notification = fetchByCondition(condition, Notification.class);
        if (notification != null) {
            return notification;
        } else {
            notification = new Notification();
            notification.setCode(notificationCode);
            notification.setMessageArguments(messageArguments);
            notification.setPersonId(Lookup.withoutLabel(personId, PersonTable.PERSON));
            return super.save(notification);
        }
    }

    public Notification createNotificationForRole(String notificationCode, List<String> messageArguments, long roleId) {
        Condition condition = createUnresolvedNotificationCondition(notificationCode, messageArguments)
                .and(NOTIFICATION.ROLE_ID.eq(roleId));
        Notification notification = fetchByCondition(condition, Notification.class);
        if (notification != null) {
            return notification;
        } else {
            notification = new Notification();
            notification.setCode(notificationCode);
            notification.setMessageArguments(messageArguments);
            notification.setRoleId(Lookup.withoutLabel(roleId, RoleTable.ROLE));
            return super.save(notification);
        }
    }

    public Notification createNotificationForAll(String notificationCode, List<String> messageArguments) {
        Condition condition = createUnresolvedNotificationCondition(notificationCode, messageArguments);
        Notification notification = fetchByCondition(condition, Notification.class);
        if (notification != null) {
            return notification;
        } else {
            notification = new Notification();
            notification.setCode(notificationCode);
            notification.setMessageArguments(messageArguments);
            return super.save(notification);
        }
    }

    public Notification fetch(long personId, long id) {
        Condition condition = NOTIFICATION.ID.eq(id).and(createPersonNotificationsCondition(personId));
        return super.fetchByCondition(condition, Notification.class);
    }

    public Slice<Map<Table.Column, Object>> fetchSlice(long personId, SliceRequest sliceRequest) {
        return super.fetchSlice(sliceRequest.addCondition(createPersonNotificationsCondition(personId)));
    }

    private Condition createUnresolvedNotificationCondition(String notificationCode, List<String> messageArguments) {
        return NOTIFICATION.RESOLVED_AT.isNull()
                .and(NOTIFICATION.CODE.eq(notificationCode))
                .and("message_arguments :: VARCHAR = ? :: VARCHAR", messageArguments.toArray());
    }

    public List<Notification> fetchUnresolvedByPersonId(long personId) {
        Condition condition = NOTIFICATION.RESOLVED_AT.isNull().and(createPersonNotificationsCondition(personId));
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        SliceRequest sliceRequest = new SliceRequest(Collections.emptyMap(), null, condition, 0, MAX_UNRESOLVED_NOTIFICATIONS_TO_SHOW, sort, null);
        return super.fetchList(sliceRequest, Notification.class);
    }

    private Condition createPersonNotificationsCondition(long personId) {
        return NOTIFICATION.PERSON_ID.isNull().and(NOTIFICATION.ROLE_ID.isNull())
                .or(NOTIFICATION.PERSON_ID.eq(personId))
                .or(NOTIFICATION.ROLE_ID.in(dslContext.select(PERSON_ROLE.ROLE_ID)
                        .from(PERSON_ROLE)
                        .where(PERSON_ROLE.PERSON_ID.eq(personId))));
    }
}
