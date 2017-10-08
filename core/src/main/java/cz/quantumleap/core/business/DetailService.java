package cz.quantumleap.core.business;

public interface DetailService<T> {

    T get(Object id);

    T save(T detail);

}
