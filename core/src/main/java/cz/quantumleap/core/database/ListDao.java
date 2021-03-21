package cz.quantumleap.core.database;

import cz.quantumleap.core.database.domain.SliceRequest;
import cz.quantumleap.core.database.domain.TableSlice;
import cz.quantumleap.core.database.entity.Entity;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SortField;
import org.jooq.Table;

import java.util.List;

import static java.util.Collections.emptyList;

public interface ListDao<TABLE extends Table<? extends Record>> {

    Entity<TABLE> getListEntity();

    TableSlice fetchSlice(SliceRequest sliceRequest);

    <T> List<T> fetchList(Condition condition, List<SortField<?>> orderBy, int limit, Class<T> type);

    default <T> List<T> fetchListByCondition(Condition condition, Class<T> type) {
        return fetchList(condition, emptyList(), Integer.MAX_VALUE, type);
    }
}
