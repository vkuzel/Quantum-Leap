package cz.quantumleap.core.data.list;

import com.google.common.collect.Lists;
import org.jooq.*;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultOrderBuilder implements OrderBuilder {

    private final Map<String, Field<?>> sortableFields;

    public DefaultOrderBuilder(Table<? extends Record> table) {
        sortableFields = Stream.of(table.fields())
                .collect(Collectors.toMap(field -> field.getName().toLowerCase(), field -> field));
    }

    @Override
    public List<SortField<?>> build(Sort sort) { // TODO It is public api (sort of) so make this optional...
        List<SortField<?>> sortFields = Lists.newArrayList();

        if (sort != null) {
            for (Sort.Order order : sort) {
                // TODO Get rid of toLowerCase...
                Field<?> field = sortableFields.get(order.getProperty().toLowerCase());
                if (field != null) {
                    SortOrder sortOrder = order.isAscending() ? SortOrder.ASC : SortOrder.DESC;
                    sortFields.add(field.sort(sortOrder));
                }
            }
        }

        return sortFields;
    }
}
