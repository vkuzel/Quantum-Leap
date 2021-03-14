package cz.quantumleap.core.notification;

import cz.quantumleap.core.database.DaoStub;
import cz.quantumleap.core.database.EntityRegistry;
import cz.quantumleap.core.database.RecordAuditor;
import cz.quantumleap.core.database.domain.SliceRequest;
import cz.quantumleap.core.database.domain.TableSlice;
import cz.quantumleap.core.database.entity.Entity;
import cz.quantumleap.core.notification.domain.Notification;
import cz.quantumleap.core.tables.NotificationTable;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.SortField;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

import static cz.quantumleap.core.tables.NotificationTable.NOTIFICATION;
import static cz.quantumleap.core.tables.PersonRoleTable.PERSON_ROLE;

@Repository
public class NotificationDao extends DaoStub<NotificationTable> {

    protected NotificationDao(DSLContext dslContext, RecordAuditor recordAuditor, EntityRegistry entityRegistry) {
        super(createEntity(), dslContext, recordAuditor, entityRegistry);
    }

    private static Entity<NotificationTable> createEntity() {
        return Entity.builder(NOTIFICATION).build();
    }

    public Notification fetch(long personId, long id) {
        Condition condition = NOTIFICATION.ID.eq(id).and(createPersonNotificationsCondition(personId));
        return super.fetchByCondition(condition, Notification.class);
    }

    public TableSlice fetchSlice(long personId, SliceRequest sliceRequest) {
        return super.fetchSlice(sliceRequest.addCondition(createPersonNotificationsCondition(personId)));
    }

    Notification fetchUnresolvedByDefinition(Notification notification) {
        String code = notification.getCode();
        List<String> arguments = notification.getMessageArguments();
        Long personId = notification.getPersonId();
        Long roleId = notification.getRoleId();

        Condition condition = NOTIFICATION.CODE.eq(code)
                .and(NOTIFICATION.RESOLVED_AT.isNull());
        if (!arguments.isEmpty()) {
            condition = condition.and("message_arguments :: VARCHAR = ? :: VARCHAR", arguments.toArray());
        } else {
            condition = condition.and("message_arguments = ARRAY[] :: VARCHAR[]", arguments.toArray());
        }
        if (personId != null) {
            condition = condition.and(NOTIFICATION.PERSON_ID.eq(personId));
        } else {
            condition = condition.and(NOTIFICATION.PERSON_ID.isNull());
        }
        if (roleId != null) {
            condition = condition.and(NOTIFICATION.ROLE_ID.eq(roleId));
        } else {
            condition = condition.and(NOTIFICATION.ROLE_ID.isNull());
        }

        return fetchByCondition(condition, Notification.class);
    }

    public List<Notification> fetchUnresolvedByPersonId(long personId, int limit) {
        Condition condition = NOTIFICATION.RESOLVED_AT.isNull().and(createPersonNotificationsCondition(personId));
        List<SortField<?>> sort = Collections.singletonList(NOTIFICATION.ID.asc());
        return super.fetchList(condition, sort, limit, Notification.class);
    }

    private Condition createPersonNotificationsCondition(long personId) {
        return NOTIFICATION.PERSON_ID.isNull().and(NOTIFICATION.ROLE_ID.isNull())
                .or(NOTIFICATION.PERSON_ID.eq(personId))
                .or(NOTIFICATION.ROLE_ID.in(dslContext.select(PERSON_ROLE.ROLE_ID)
                        .from(PERSON_ROLE)
                        .where(PERSON_ROLE.PERSON_ID.eq(personId))));
    }
}
