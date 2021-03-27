package cz.quantumleap.core.database.query;

import cz.quantumleap.core.database.domain.FetchParams;
import cz.quantumleap.core.database.domain.Slice;
import cz.quantumleap.core.database.entity.Entity;
import cz.quantumleap.core.database.entity.EntityIdentifier;
import cz.quantumleap.core.database.entity.FieldMetaType;
import cz.quantumleap.core.database.entity.LookupMetaType;
import cz.quantumleap.core.slicequery.domain.SliceQuery;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static cz.quantumleap.core.database.query.QueryUtils.resolveLookupFieldName;
import static java.lang.String.join;

public final class SliceFactory {

    private final Entity<?> entity;

    public SliceFactory(Entity<?> entity) {
        this.entity = entity;
    }

    public Slice forRequestedResult(
            FetchParams fetchParams,
            Result<?> result,
            List<SliceQuery> sliceQueries
    ) {
        Map<Field<?>, Slice.Column> fieldColumnMap = createFieldColumnMap(fetchParams.getSort());
        List<Slice.Column> columns = new ArrayList<>(fieldColumnMap.values());
        List<Field<?>> fields = new ArrayList<>(fieldColumnMap.keySet());
        int maxSize = fetchParams.getSize();

        List<List<Object>> rows = new ArrayList<>(result.size());
        for (Record record : result) {
            if (rows.size() < maxSize) {
                rows.add(createRow(fields, record));
            }
        }

        return new Slice(
                entity.getIdentifier(),
                fetchParams,
                sliceQueries,
                result.size() > maxSize,
                columns,
                rows
        );
    }

    private Map<Field<?>, Slice.Column> createFieldColumnMap(Sort sort) {
        List<Field<?>> primaryKeyFields = entity.getPrimaryKeyFields();
        List<Field<?>> fields = createFieldsList();

        Map<Field<?>, Slice.Column> fieldColumnMap = new LinkedHashMap<>(fields.size());
        for (Field<?> field : fields) {
            FieldMetaType fieldMetaType = entity.getFieldMetaType(field);
            String fieldName = field.getName();
            Sort.Order order = sort != null ? sort.getOrderFor(fieldName) : null;

            Slice.Column column;
            if (fieldMetaType instanceof LookupMetaType) {
                String lookupFieldName = resolveLookupFieldName(field);
                column = new Slice.LookupColumn(
                        lookupFieldName,
                        order,
                        fieldMetaType.asLookup().getEntityIdentifier()
                );
            } else {
                column = new Slice.Column(
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

    private List<Field<?>> createFieldsList() {
        List<String> fieldNames = entity.getDefaultSliceFieldNames();
        if (fieldNames == null) {
            return entity.getFields();
        }

        Map<String, Field<?>> fieldMap = entity.getFieldMap();
        List<Field<?>> fields = new ArrayList<>(fieldNames.size());
        for (String fieldName : fieldNames) {
            Field<?> field = fieldMap.get(fieldName);
            if (field == null) {
                String msg = "Field %s not found for entity %s with fields %s";
                throw new IllegalStateException(String.format(msg, fieldName, entity, join(", ", fieldMap.keySet())));
            }
            fields.add(field);
        }
        return fields;
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

                Slice.Lookup lookup = new Slice.Lookup(id, label, entityIdentifier);
                row.add(lookup);
            } else {
                Object value = record.get(field);
                row.add(value);
            }
        }
        return row;
    }
}
