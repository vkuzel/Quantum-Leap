package cz.quantumleap.core.business;

import org.springframework.validation.Errors;

public interface DetailService<T> {

    T get(Object id);

    T save(T detail, Errors errors);
}
