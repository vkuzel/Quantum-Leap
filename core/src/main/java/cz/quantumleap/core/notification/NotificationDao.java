package cz.quantumleap.core.notification;

import cz.quantumleap.core.database.DaoStub;
import cz.quantumleap.core.database.EntityRegistry;
import cz.quantumleap.core.database.domain.FetchParams;
import cz.quantumleap.core.database.domain.Slice;
import cz.quantumleap.core.database.entity.Entity;
import cz.quantumleap.core.notification.domain.Notification;
import cz.quantumleap.core.slicequery.SliceQueryDao;
import cz.quantumleap.core.tables.NotificationTable;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

import static cz.quantumleap.core.tables.NotificationTable.NOTIFICATION;
import static cz.quantumleap.core.tables.PersonRoleTable.PERSON_ROLE;
import static org.springframework.data.domain.Sort.Direction.ASC;

@Repository
public class NotificationDao extends DaoStub<NotificationTable> {

    protected NotificationDao(DSLContext dslContext, EntityRegistry entityRegistry, SliceQueryDao sliceQueryDao) {
        super(createEntity(), dslContext, entityRegistry, sliceQueryDao);
    }

    private static Entity<NotificationTable> createEntity() {
        return Entity.builder(NOTIFICATION)
                .setDefaultSliceFieldNames("id", "code", "message_arguments", "created_at", "resolved_at")
                .build();
    }

    public Notification fetch(long personId, long id) {
        Condition condition = NOTIFICATION.ID.eq(id).and(createPersonNotificationsCondition(personId));
        return super.fetchByCondition(condition, Notification.class);
    }

    public Slice fetchSlice(long personId, FetchParams fetchParams) {
        return super.fetchSlice(fetchParams.addCondition(createPersonNotificationsCondition(personId)));
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
        Sort sort = Sort.by(ASC, "id");
        FetchParams params = FetchParams.empty().addCondition(condition).withSort(sort).withSize(limit);
        return super.fetchList(params, Notification.class);
    }

    private Condition createPersonNotificationsCondition(long personId) {
        return NOTIFICATION.PERSON_ID.isNull().and(NOTIFICATION.ROLE_ID.isNull())
                .or(NOTIFICATION.PERSON_ID.eq(personId))
                .or(NOTIFICATION.ROLE_ID.in(dslContext.select(PERSON_ROLE.ROLE_ID)
                        .from(PERSON_ROLE)
                        .where(PERSON_ROLE.PERSON_ID.eq(personId))));
    }
}
