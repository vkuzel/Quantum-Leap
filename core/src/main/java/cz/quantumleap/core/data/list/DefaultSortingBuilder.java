package cz.quantumleap.core.data.list;

import com.google.common.collect.Lists;
import org.jooq.*;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultSortingBuilder implements SortingBuilder {

    private final Map<String, Field<?>> sortableFields;
    private final Field<String> labelField;

    public DefaultSortingBuilder(Table<? extends Record> table, Field<String> labelField) {
        sortableFields = Stream.of(table.fields())
                .collect(Collectors.toMap(field -> field.getName().toLowerCase(), field -> field));
        this.labelField = labelField;
    }

    @Override
    public List<SortField<?>> build(Sort sort) {
        if (sort.isUnsorted()) {
            return Collections.emptyList();
        }

        List<SortField<?>> sortFields = Lists.newArrayList();
        for (Sort.Order order : sort) {
            // TODO Get rid of toLowerCase...
            Field<?> field = sortableFields.get(order.getProperty().toLowerCase());
            if (field != null) {
                SortOrder sortOrder = order.isAscending() ? SortOrder.ASC : SortOrder.DESC;
                sortFields.add(field.sort(sortOrder));
            }
        }

        return sortFields;
    }

    @Override
    public List<SortField<?>> buildForLookup() {
        return Collections.singletonList(labelField.asc());
    }
}
