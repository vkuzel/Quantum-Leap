package cz.quantumleap.core.database.query;

import cz.quantumleap.core.database.domain.SliceRequest;
import cz.quantumleap.core.database.domain.TablePreferences;
import cz.quantumleap.core.database.domain.TableSlice;
import cz.quantumleap.core.database.domain.TableSlice.Column;
import cz.quantumleap.core.database.domain.TableSlice.Lookup;
import cz.quantumleap.core.database.domain.TableSlice.LookupColumn;
import cz.quantumleap.core.database.entity.Entity;
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

import static cz.quantumleap.core.database.query.QueryUtils.resolveLookupIdFieldName;

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
        Map<Field<?>, Column> fieldColumnMap = createFieldColumnMap(result, sliceRequest.getSort());
        List<Column> columns = new ArrayList<>(fieldColumnMap.values());
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

    private Map<Field<?>, Column> createFieldColumnMap(Result<?> result, Sort sort) {
        List<Field<?>> primaryKeyFields = entity.getPrimaryKeyFields();
        Field<?>[] fields = result.fields();

        Map<Field<?>, Column> fieldColumnMap = new LinkedHashMap<>(fields.length);
        for (Field<?> field : fields) {
            FieldMetaType fieldMetaType = entity.getFieldMetaType(field);
            String fieldName = field.getName();
            Sort.Order order = sort != null ? sort.getOrderFor(fieldName) : null;

            Column column;
            if (fieldMetaType instanceof LookupMetaType) {
                column = new LookupColumn(
                        fieldName,
                        order,
                        fieldMetaType.asLookup().getEntityIdentifier()
                );
            } else {
                column = new Column(
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
                Object value = record.get(field);
                String idFieldName = resolveLookupIdFieldName(field);
                Field<?> idField = record.field(idFieldName);
                if (idField != null) {
                    Object id = record.get(idField);
                    value = new Lookup(
                            id,
                            value != null ? value.toString() : (id != null ? id.toString() : null),
                            fieldMetaType.asLookup().getEntityIdentifier()
                    );
                }
                row.add(value);
            } else {
                Object value = record.get(field);
                row.add(value);
            }
        }
        return row;
    }
}
