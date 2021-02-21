package cz.quantumleap.core.data.query;

import cz.quantumleap.core.data.transport.SliceRequest;
import org.jooq.Field;
import org.jooq.SortField;
import org.jooq.SortOrder;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import java.util.*;

import static org.jooq.SortOrder.ASC;
import static org.jooq.SortOrder.DESC;

public class SortingFactory {

    private final Map<String, Field<?>> sortableFieldMap;

    public SortingFactory(List<Field<?>> fields) {
        this.sortableFieldMap = createSortableFieldMap(fields);
    }

    public List<SortField<?>> forSliceRequest(SliceRequest request) {
        Sort sort = request.getSort();
        if (sort.isUnsorted()) {
            return Collections.emptyList();
        }

        List<SortField<?>> sortFields = new ArrayList<>();
        for (Order order : sort) {
            String name = normalizeFieldName(order.getProperty());
            Field<?> field = sortableFieldMap.get(name);
            if (field == null) {
                String names = String.join(", ", sortableFieldMap.keySet());
                throw new IllegalStateException("Field " + name + " was not found in " + names);
            }

            SortOrder sortOrder = order.isAscending() ? ASC : DESC;
            sortFields.add(field.sort(sortOrder));
        }

        return sortFields;
    }

    private Map<String, Field<?>> createSortableFieldMap(List<Field<?>> fields) {
        Map<String, Field<?>> fieldMap = new HashMap<>(fields.size());
        for (Field<?> field : fields) {
            String name = normalizeFieldName(field.getName());
            fieldMap.put(name, field);
        }
        return fieldMap;
    }

    private String normalizeFieldName(String name) {
        return name.toLowerCase();
    }
}
