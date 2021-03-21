package cz.quantumleap.core.business;

import cz.quantumleap.core.database.domain.FetchParams;
import cz.quantumleap.core.database.domain.TableSlice;
import cz.quantumleap.core.database.entity.EntityIdentifier;
import org.jooq.Record;
import org.jooq.Table;

public interface ListService {

    <TABLE extends Table<? extends Record>> EntityIdentifier<?> getListEntityIdentifier(Class<TABLE> type);

    TableSlice findSlice(FetchParams fetchParams);
}
