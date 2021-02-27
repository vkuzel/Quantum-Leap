package cz.quantumleap.core.business;

import cz.quantumleap.core.database.entity.EntityIdentifier;
import org.jooq.Record;
import org.jooq.Table;
import org.springframework.validation.Errors;

public interface DetailService<T> {

    <TABLE extends Table<? extends Record>> EntityIdentifier<TABLE> getDetailEntityIdentifier(Class<TABLE> type);

    T get(Object id);

    T save(T detail, Errors errors);
}
