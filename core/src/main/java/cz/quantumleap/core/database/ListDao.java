package cz.quantumleap.core.database;

import cz.quantumleap.core.database.entity.Entity;
import cz.quantumleap.core.database.entity.EntityIdentifier;
import cz.quantumleap.core.database.transport.SliceRequest;
import cz.quantumleap.core.database.transport.TableSlice;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Table;

import java.util.List;

public interface ListDao<TABLE extends Table<? extends Record>> {

    Entity<TABLE> getListEntity();

    default EntityIdentifier<TABLE> getListEntityIdentifier() {
        return getListEntity().getIdentifier();
    }

    // TODO Rename to fetchTableSlice()
    TableSlice fetchSlice(SliceRequest sliceRequest);

    <T> List<T> fetchList(SliceRequest sliceRequest, Class<T> type);

    <T> List<T> fetchListByCondition(Condition condition, Class<T> type);
}
