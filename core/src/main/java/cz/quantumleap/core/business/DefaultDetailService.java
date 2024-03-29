package cz.quantumleap.core.business;

import cz.quantumleap.core.utils.Utils;
import cz.quantumleap.core.database.DetailDao;
import cz.quantumleap.core.database.entity.EntityIdentifier;
import org.jooq.Record;
import org.jooq.Table;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;

public class DefaultDetailService<T> implements DetailService<T> {

    private final Class<T> transportType;
    private final DetailDao<?> detailDao;

    public DefaultDetailService(Class<T> transportType, DetailDao<?> detailDao) {
        this.transportType = transportType;
        this.detailDao = detailDao;
    }

    @Override
    public <TABLE extends Table<? extends Record>> EntityIdentifier<TABLE> getDetailEntityIdentifier(Class<TABLE> type) {
        var entityIdentifier = detailDao.getDetailEntity().getIdentifier();
        return Utils.checkTableType(entityIdentifier, type);
    }

    @Override
    public T get(Object id) {
        var detail = detailDao.fetchById(id, transportType);
        if (detail != null) {
            return detail;
        } else {
            throw new IllegalArgumentException("Entity " + transportType.getSimpleName() + " was not found for id " + id);
        }
    }

    @Transactional
    @Override
    public T save(T detail, Errors errors) {
        if (!errors.hasErrors()) {
            return detailDao.save(detail);
        } else {
            return detail;
        }
    }
}
