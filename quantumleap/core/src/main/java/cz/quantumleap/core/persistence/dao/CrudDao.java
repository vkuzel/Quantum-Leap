package cz.quantumleap.core.persistence.dao;

import org.jooq.Condition;

import java.util.Optional;

public interface CrudDao {

    <T> Optional<T> fetchById(Object id, Class<T> type);

    <T> Optional<T> fetchByCondition(Condition condition, Class<T> type);

    <T> T save(T transport);

    void deleteById(Object id);
}