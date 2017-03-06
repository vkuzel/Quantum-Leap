package cz.quantumleap.core.persistence.dao.collection;

import cz.quantumleap.core.persistence.transport.SliceRequest;
import org.jooq.SortField;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface OrderBuilder {

    List<SortField<?>> build(Sort sort);
}
