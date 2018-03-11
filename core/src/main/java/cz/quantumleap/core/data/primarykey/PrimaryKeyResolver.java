package cz.quantumleap.core.data.primarykey;

import org.jooq.Field;

import java.util.List;

public interface PrimaryKeyResolver {

    Field<Object> getPrimaryKeyField();

    List<Field<Object>> getPrimaryKeyFields();
}
