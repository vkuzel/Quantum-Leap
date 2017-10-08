package cz.quantumleap.core.business;

import cz.quantumleap.core.data.DetailDao;
import org.springframework.transaction.annotation.Transactional;

public final class DefaultDetailService<T> implements DetailService<T> {

    private final Class<T> transportType;
    private final DetailDao<?> detailDao;

    public DefaultDetailService(Class<T> transportType, DetailDao<?> detailDao) {
        this.transportType = transportType;
        this.detailDao = detailDao;
    }

    @Override
    public T get(Object id) {
        return detailDao.fetchById(id, transportType)
                .orElseThrow(() -> new IllegalArgumentException("Entity " + transportType.getSimpleName() + " was not found for id " + id));
    }

    @Transactional
    @Override
    public T save(T detail) {
        return detailDao.save(detail);
    }
}
