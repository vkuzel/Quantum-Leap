package cz.quantumleap.core.data;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableField;

import java.util.Collection;
import java.util.List;

public interface DetailDao<TABLE extends Table<? extends Record>> {

    <T> T fetchById(Object id, Class<T> type);

    <T> T fetchByCondition(Condition condition, Class<T> type);

    <T> T save(T detail);

    void deleteById(Object id);

    void deleteByCondition(Condition condition);

    <T> List<T> saveDetailsAssociatedBy(TableField foreignKey, Object foreignId, Collection<T> details, Class<T> detailType);
}