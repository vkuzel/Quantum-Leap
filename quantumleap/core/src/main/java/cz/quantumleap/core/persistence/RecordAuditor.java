package cz.quantumleap.core.persistence;

import org.jooq.*;
import org.jooq.impl.DefaultRecordListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RecordAuditor implements RecordListenerProvider {

    private static final String CREATED_AT_FIELD_NAME = "created_at";
    private static final String UPDATED_AT_FIELD_NAME = "updated_at";
    private static final String CREATED_BY_FIELD_NAME = "created_by";
    private static final String UPDATED_BY_FIELD_NAME = "updated_by";
    private static final String REVISION_FIELD_NAME = "revision";

    public void onInsert(Record record) {
        if (record instanceof TableRecord) {
            TableRecord tableRecord = (TableRecord) record;

            setField(CREATED_AT_FIELD_NAME, LocalDateTime.now(), tableRecord);
            setField(CREATED_BY_FIELD_NAME, getCurrentUser(), tableRecord);
            setField(REVISION_FIELD_NAME, 1, tableRecord);

            resetField(UPDATED_AT_FIELD_NAME, tableRecord);
            resetField(UPDATED_BY_FIELD_NAME, tableRecord);
        }
    }

    public void onUpdate(Record record) {
        if (record instanceof TableRecord) {
            TableRecord tableRecord = (TableRecord) record;

            setField(UPDATED_AT_FIELD_NAME, LocalDateTime.now(), tableRecord);
            setField(UPDATED_BY_FIELD_NAME, getCurrentUser(), tableRecord);
            Long revision = getFieldValue(REVISION_FIELD_NAME, tableRecord, Long.class);
            if (revision != null) {
                setField(REVISION_FIELD_NAME, revision + 1, tableRecord);
            }

            resetField(CREATED_AT_FIELD_NAME, tableRecord);
            resetField(CREATED_BY_FIELD_NAME, tableRecord);
        }
    }

    private void setField(String fieldName, Object value, TableRecord record) {
        Table table = record.getTable();
        Field<Object> field = (Field<Object>) table.field(fieldName);
        if (field != null) {
            record.set(field, value);
        }
    }

    private <T> T getFieldValue(String fieldName, TableRecord record, Class<T> type) {
        Field<?> field = record.field(fieldName);
        if (field != null) {
            return record.get(field, type);
        }
        return null;
    }

    private void resetField(String fieldName, TableRecord record) {
        Field<?> field = record.field(fieldName);
        if (field != null) {
            record.reset(field);
        }
    }

    private String getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // TODO Get user's email here!
        return authentication.getName();
    }

    @Override
    public RecordListener provide() {
        return new AuditingRecordListener();
    }

    public class AuditingRecordListener extends DefaultRecordListener {

        @Override
        public void insertStart(RecordContext ctx) {
            Record record = ctx.record();
            onInsert(record);
        }

        @Override
        public void updateStart(RecordContext ctx) {
            Record record = ctx.record();
            onUpdate(record);
        }
    }
}
