package cz.quantumleap.core.data.list;

import org.jooq.SortField;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface SortingBuilder {

    List<SortField<?>> build(Sort sort);

    List<SortField<?>> buildForLookup();
}
