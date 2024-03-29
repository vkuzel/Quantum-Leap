package cz.quantumleap.core.database.query;

import cz.quantumleap.core.database.domain.FetchParams;
import org.jooq.Field;
import org.jooq.SortField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static cz.quantumleap.core.database.query.QueryUtils.getFieldSafely;
import static org.jooq.SortOrder.ASC;
import static org.jooq.SortOrder.DESC;

public final class SortingFactory {

    private final Map<String, Field<?>> fieldMap;

    public SortingFactory(Map<String, Field<?>> fieldMap) {
        this.fieldMap = fieldMap;
    }

    public List<SortField<?>> forFetchParams(FetchParams params) {
        var sort = params.getSort();
        if (sort == null || sort.isUnsorted()) {
            return Collections.emptyList();
        }

        List<SortField<?>> sortFields = new ArrayList<>();
        for (var order : sort) {
            Field<?> field = getFieldSafely(fieldMap, order.getProperty());
            var sortOrder = order.isAscending() ? ASC : DESC;
            sortFields.add(field.sort(sortOrder));
        }

        return sortFields;
    }
}
