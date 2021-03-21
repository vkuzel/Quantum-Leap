package cz.quantumleap.core.database;

import cz.quantumleap.core.database.entity.Entity;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableField;

import java.util.Collection;
import java.util.List;

public interface DetailDao<TABLE extends Table<? extends Record>> {

    Entity<TABLE> getDetailEntity();

    <T> T fetchById(Object id, Class<T> type);

    <T> T fetchByCondition(Condition condition, Class<T> type);

    <T> T save(T detail);

    <T> List<T> saveAll(List<T> details);

    int deleteById(Object id);

    int deleteByCondition(Condition condition);

    <T, F> List<T> saveDetailsAssociatedBy(TableField<?, F> foreignKey, F foreignId, Collection<T> details, Class<T> detailType);
}
