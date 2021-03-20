package cz.quantumleap.core.database.query;

import cz.quantumleap.core.database.domain.SliceRequest;
import cz.quantumleap.core.database.domain.TablePreferences;
import cz.quantumleap.core.database.domain.TableSlice;
import cz.quantumleap.core.database.entity.Entity;
import cz.quantumleap.core.database.entity.EntityIdentifier;
import cz.quantumleap.core.database.entity.FieldMetaType;
import cz.quantumleap.core.database.entity.LookupMetaType;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static cz.quantumleap.core.database.query.QueryUtils.resolveLookupFieldName;

public final class TableSliceFactory {

    private final Entity<?> entity;

    public TableSliceFactory(Entity<?> entity) {
        this.entity = entity;
    }

    public TableSlice forRequestedResult(
            TablePreferences tablePreferences,
            SliceRequest sliceRequest,
            Result<?> result
    ) {
        Map<Field<?>, TableSlice.Column> fieldColumnMap = createFieldColumnMap(sliceRequest.getSort());
        List<TableSlice.Column> columns = new ArrayList<>(fieldColumnMap.values());
        List<Field<?>> fields = new ArrayList<>(fieldColumnMap.keySet());
        int maxSize = sliceRequest.getSize();

        List<List<Object>> rows = new ArrayList<>(result.size());
        for (Record record : result) {
            if (rows.size() < maxSize) {
                rows.add(createRow(fields, record));
            }
        }

        return new TableSlice(
                entity.getIdentifier(),
                tablePreferences,
                sliceRequest,
                result.size() > maxSize,
                columns,
                rows
        );
    }

    private Map<Field<?>, TableSlice.Column> createFieldColumnMap(Sort sort) {
        List<Field<?>> primaryKeyFields = entity.getPrimaryKeyFields();
        List<Field<?>> fields = entity.getFields();

        Map<Field<?>, TableSlice.Column> fieldColumnMap = new LinkedHashMap<>(fields.size());
        for (Field<?> field : fields) {
            FieldMetaType fieldMetaType = entity.getFieldMetaType(field);
            String fieldName = field.getName();
            Sort.Order order = sort != null ? sort.getOrderFor(fieldName) : null;

            TableSlice.Column column;
            if (fieldMetaType instanceof LookupMetaType) {
                String lookupFieldName = resolveLookupFieldName(field);
                column = new TableSlice.LookupColumn(
                        lookupFieldName,
                        order,
                        fieldMetaType.asLookup().getEntityIdentifier()
                );
            } else {
                column = new TableSlice.Column(
                        field.getType(),
                        fieldName,
                        primaryKeyFields.contains(field),
                        order
                );
            }
            fieldColumnMap.put(field, column);
        }
        return fieldColumnMap;
    }

    private List<Object> createRow(List<Field<?>> fields, Record record) {
        List<Object> row = new ArrayList<>(record.size());
        for (Field<?> field : fields) {
            FieldMetaType fieldMetaType = entity.getFieldMetaType(field);
            if (fieldMetaType instanceof LookupMetaType) {
                Object id = record.get(field);
                String lookupFieldName = resolveLookupFieldName(field);
                Field<?> lookupField = record.field(lookupFieldName);

                String label = record.get(lookupField, String.class);
                label = label != null ? label : (id != null ? id.toString() : null);
                EntityIdentifier<?> entityIdentifier = fieldMetaType.asLookup().getEntityIdentifier();

                TableSlice.Lookup lookup = new TableSlice.Lookup(id, label, entityIdentifier);
                row.add(lookup);
            } else {
                Object value = record.get(field);
                row.add(value);
            }
        }
        return row;
    }
}
