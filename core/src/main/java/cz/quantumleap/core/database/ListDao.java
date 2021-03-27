package cz.quantumleap.core.database;

import cz.quantumleap.core.database.domain.FetchParams;
import cz.quantumleap.core.database.domain.Slice;
import cz.quantumleap.core.database.entity.Entity;
import org.jooq.Record;
import org.jooq.Table;

import java.util.List;

public interface ListDao<TABLE extends Table<? extends Record>> {

    Entity<TABLE> getListEntity();

    Slice fetchSlice(FetchParams fetchParams);

    <T> List<T> fetchList(FetchParams fetchParams, Class<T> type);
}
