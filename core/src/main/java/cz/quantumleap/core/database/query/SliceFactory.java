package cz.quantumleap.core.database.query;

import cz.quantumleap.core.database.domain.FetchParams;
import cz.quantumleap.core.database.domain.Slice;
import cz.quantumleap.core.database.entity.Entity;
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
        var fieldColumnMap = createFieldColumnMap(fetchParams.getSort());
        List<Slice.Column> columns = new ArrayList<>(fieldColumnMap.values());
        List<Field<?>> fields = new ArrayList<>(fieldColumnMap.keySet());
        var maxSize = fetchParams.getSize();

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
        var primaryKeyFields = entity.getPrimaryKeyFields();
        var fields = createFieldsList();

        Map<Field<?>, Slice.Column> fieldColumnMap = new LinkedHashMap<>(fields.size());
        for (var field : fields) {
            var fieldMetaType = entity.getFieldMetaType(field);
            var fieldName = field.getName();
            var order = sort != null ? sort.getOrderFor(fieldName) : null;

            Slice.Column column;
            if (fieldMetaType instanceof LookupMetaType) {
                var lookupFieldName = resolveLookupFieldName(field);
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
        var fieldNames = entity.getDefaultSliceFieldNames();
        if (fieldNames == null) {
            return entity.getFields();
        }

        var fieldMap = entity.getFieldMap();
        List<Field<?>> fields = new ArrayList<>(fieldNames.size());
        for (var fieldName : fieldNames) {
            var field = fieldMap.get(fieldName);
            if (field == null) {
                var msg = "Field %s not found for entity %s with fields %s";
                throw new IllegalStateException(String.format(msg, fieldName, entity, join(", ", fieldMap.keySet())));
            }
            fields.add(field);
        }
        return fields;
    }

    private List<Object> createRow(List<Field<?>> fields, Record record) {
        List<Object> row = new ArrayList<>(record.size());
        for (var field : fields) {
            var fieldMetaType = entity.getFieldMetaType(field);
            if (fieldMetaType instanceof LookupMetaType) {
                var id = record.get(field);
                var lookupFieldName = resolveLookupFieldName(field);
                var lookupField = record.field(lookupFieldName);

                var label = record.get(lookupField, String.class);
                label = label != null ? label : (id != null ? id.toString() : null);
                var entityIdentifier = fieldMetaType.asLookup().getEntityIdentifier();

                var lookup = new Slice.Lookup(id, label, entityIdentifier);
                row.add(lookup);
            } else {
                var value = record.get(field);
                row.add(value);
            }
        }
        return row;
    }
}
