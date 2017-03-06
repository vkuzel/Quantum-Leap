package cz.quantumleap.core.persistence.collection;

import org.jooq.SortField;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface OrderBuilder {

    List<SortField<?>> build(Sort sort);
}
