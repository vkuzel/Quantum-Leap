package cz.quantumleap.core.persistence;

import org.jooq.Record;
import org.jooq.RecordContext;
import org.jooq.RecordListener;
import org.jooq.RecordListenerProvider;
import org.jooq.impl.DefaultRecordListener;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Component
public class RecordAuditor implements RecordListenerProvider {

    @Override
    public RecordListener provide() {
        return new AuditingRecordListener();
    }

    public static class AuditingRecordListener extends DefaultRecordListener {

        // TODO Real username provider! Maybe use SecurityContextHolder with ACL...
        private static final String username = "pepa";

        @Override
        public void insertStart(RecordContext ctx) {
            Record record = ctx.record();
            for (Method method : record.getClass().getDeclaredMethods()) {
                try {
                    // TODO Externalize audit fields ... will be needed in the DAO...!
                    // TODO This is not going to work since I have a generic records... I need to "listen" on fields.
                    switch (method.getName()) {
                        case "setCreatedAt":
                            method.invoke(record, LocalDateTime.now());
                            break;
                        case "setCreatedBy":
                            method.invoke(record, username);
                            break;
                        case "setRevision":
                            method.invoke(record, 1);
                            break;
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }

        @Override
        public void updateStart(RecordContext ctx) {
            Record record = ctx.record();
            for (Method method : record.getClass().getDeclaredMethods()) {
                try {
                    switch (method.getName()) {
                        case "setUpdatedAt":
                            method.invoke(record, LocalDateTime.now());
                            break;
                        case "setUpdatedBy":
                            method.invoke(record, username);
                            break;
                        case "setRevision":
                            Method getRevisionMethod = record.getClass().getDeclaredMethod("getRevision");
                            int revision = (int) getRevisionMethod.invoke(record);
                            method.invoke(record, revision + 1);
                            break;
                    }
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }
    }
}
