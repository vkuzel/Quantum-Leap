package cz.quantumleap.core.persistence;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Table;

import java.util.Optional;

public interface CrudDao<TABLE extends Table<? extends Record>> {

    <T> Optional<T> fetchById(Object id, Class<T> type);

    <T> Optional<T> fetchByCondition(Condition condition, Class<T> type);

    <T> T save(T transport);

    void deleteById(Object id);
}