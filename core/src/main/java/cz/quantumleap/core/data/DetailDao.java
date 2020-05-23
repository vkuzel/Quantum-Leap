package cz.quantumleap.core.data;

import cz.quantumleap.core.data.entity.EntityIdentifier;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableField;

import java.util.Collection;
import java.util.List;

public interface DetailDao<TABLE extends Table<? extends Record>> {

    EntityIdentifier<TABLE> getDetailEntityIdentifier();

    <T> T fetchById(Object id, Class<T> type);

    <T> T fetchByCondition(Condition condition, Class<T> type);

    <T> T save(T detail);

    <T> List<T> saveAll(List<T> details);

    int deleteById(Object id);

    int deleteByCondition(Condition condition);

    <T> List<T> saveDetailsAssociatedBy(TableField foreignKey, Object foreignId, Collection<T> details, Class<T> detailType);
}
