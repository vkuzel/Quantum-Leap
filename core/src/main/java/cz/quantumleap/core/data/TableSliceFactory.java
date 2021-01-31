package cz.quantumleap.core.data;

import cz.quantumleap.core.data.entity.Entity;
import cz.quantumleap.core.data.entity.EntityIdentifier;
import cz.quantumleap.core.data.transport.SliceRequest;
import cz.quantumleap.core.data.transport.TablePreferences;
import cz.quantumleap.core.data.transport.TableSlice;
import cz.quantumleap.core.data.transport.TableSlice.Column;
import cz.quantumleap.core.data.transport.TableSlice.LookupColumn;
import org.jooq.*;
import org.springframework.data.domain.Sort;

import java.util.*;

public class TableSliceFactory {

    private final Entity<?> entity;
    private final List<TablePreferences> tablePreferencesList;
    private final SliceRequest sliceRequest;

    public TableSliceFactory(Entity<?> entity, List<TablePreferences> tablePreferencesList, SliceRequest sliceRequest) {
        this.entity = entity;
        this.tablePreferencesList = tablePreferencesList;
        this.sliceRequest = sliceRequest;
    }

    public TableSlice createTableSlice(Result<?> result) {
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
                selectTablePreferences(),
                sliceRequest,
                result.size() > maxSize,
                columns,
                rows
        );
    }

    private Map<Field<?>, Column> createFieldColumnMap(Result<?> result, Sort sort) {
        List<Field<?>> primaryKeyFields = entity.getPrimaryKeyFields();
        Map<Field<?>, EntityIdentifier<?>> lookupIdentifierMap = getLookupIdentifierMap();
        Field<?>[] fields = result.fields();

        Map<Field<?>, Column> fieldColumnMap = new LinkedHashMap<>(fields.length);
        for (Field<?> field : fields) {
            String fieldName = field.getName();
            Sort.Order order = sort != null ? sort.getOrderFor(fieldName) : null;

            Column column;
            if (lookupIdentifierMap.containsKey(field)) {
                column = new LookupColumn(
                        fieldName,
                        order,
                        lookupIdentifierMap.get(field)
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
            Object value = record.get(field);
            // TODO Create lookup value
            row.add(value);
        }
        return row;
    }

    private TablePreferences selectTablePreferences() {
        for (TablePreferences preferences : tablePreferencesList) {
            if (preferences.isDefault()) {
                return preferences;
            }
        }
        return TablePreferences.EMPTY;
    }

    private Map<Field<?>, EntityIdentifier<?>> getLookupIdentifierMap() {
        Map<Field<?>, EntityIdentifier<?>> map = new HashMap<>();

        for (ForeignKey<? extends Record, ?> foreignKey : entity.getTable().getReferences()) {
            if (foreignKey.getFieldsArray().length != 1) {
                continue;
            }

            TableField<? extends Record, ?> tableField = foreignKey.getFieldsArray()[0];
            EntityIdentifier<?> defaultEntityIdentifier = EntityIdentifier.forTable(foreignKey.getKey().getTable());
            EntityIdentifier<?> entityIdentifier = entity.getLookupFieldsMap().getOrDefault(tableField, defaultEntityIdentifier);

            map.put(tableField, entityIdentifier);
        }
        return map;
    }
}
