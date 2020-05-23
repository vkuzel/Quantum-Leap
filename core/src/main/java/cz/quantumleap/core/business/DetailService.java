package cz.quantumleap.core.business;

import cz.quantumleap.core.data.entity.EntityIdentifier;
import org.springframework.validation.Errors;

public interface DetailService<T> {

    EntityIdentifier<?> getDetailEntityIdentifier();

    T get(Object id);

    T save(T detail, Errors errors);
}
