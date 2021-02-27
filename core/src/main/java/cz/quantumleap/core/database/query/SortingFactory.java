package cz.quantumleap.core.database.query;

import cz.quantumleap.core.database.domain.SliceRequest;
import org.jooq.Field;
import org.jooq.SortField;
import org.jooq.SortOrder;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static cz.quantumleap.core.database.query.QueryUtils.createFieldMap;
import static cz.quantumleap.core.database.query.QueryUtils.normalizeFieldName;
import static org.jooq.SortOrder.ASC;
import static org.jooq.SortOrder.DESC;

public final class SortingFactory {

    private final Map<String, Field<?>> fieldMap;

    public SortingFactory(List<Field<?>> fields) {
        this.fieldMap = createFieldMap(fields);
    }

    public List<SortField<?>> forSliceRequest(SliceRequest request) {
        Sort sort = request.getSort();
        if (sort.isUnsorted()) {
            return Collections.emptyList();
        }

        List<SortField<?>> sortFields = new ArrayList<>();
        for (Order order : sort) {
            String name = normalizeFieldName(order.getProperty());
            Field<?> field = fieldMap.get(name);
            if (field == null) {
                String names = String.join(", ", fieldMap.keySet());
                throw new IllegalStateException("Field " + name + " was not found in " + names);
            }

            SortOrder sortOrder = order.isAscending() ? ASC : DESC;
            sortFields.add(field.sort(sortOrder));
        }

        return sortFields;
    }
}
