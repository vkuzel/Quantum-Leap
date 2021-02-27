package cz.quantumleap.core.database;

import cz.quantumleap.core.common.ReflectionUtils;
import org.jooq.*;
import org.jooq.types.YearToSecond;

import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;

public class RecordFactory<T> {

    private final DSLContext dslContext;
    private final Class<T> transportType;
    private final Table<?> table;

    private final Set<String> transportFieldNames;
    private final ConverterProvider converterProvider;
    private final boolean customConvertibleTypes;

    public RecordFactory(DSLContext dslContext, Class<T> transportType, Table<?> table) {
        this.dslContext = dslContext;
        this.transportType = transportType;
        this.table = table;

        this.transportFieldNames = ReflectionUtils.getClassFields(transportType).stream()
                .map(java.lang.reflect.Field::getName).collect(Collectors.toSet());
        this.converterProvider = dslContext.configuration().converterProvider();
        this.customConvertibleTypes = hasCustomConvertibleTypes();
    }

    public Record createRecord(T transport) {
        Record record = dslContext.newRecord(table);
        if (customConvertibleTypes) {
            for (Field<?> field : record.fields()) {
                setValueToRecordField(transport, field, record);
            }
        } else {
            record.from(transport);
        }
        return record;
    }

    private void setValueToRecordField(T transport, Field<?> field, Record record) {
        String transportFieldName = LOWER_UNDERSCORE.to(LOWER_CAMEL, field.getName().toLowerCase());
        if (transportFieldNames.contains(transportFieldName)) {
            DataType<?> databaseType = field.getDataType();
            Object value = ReflectionUtils.getClassFieldValue(transportType, transport, transportFieldName);
            if (value != null) {
                Class<?> userType = value.getClass();
                Converter<Object, Object> converter = resolveConverter(databaseType, userType);
                record.setValue(castField(field), value, converter);
            } else if (databaseType.nullable()) {
                record.setValue(castField(field), null);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Converter<Object, Object> resolveConverter(DataType<?> dataType, Class<?> userType) {
        Class<?> databaseType = dataType.getType();
        return (Converter<Object, Object>) converterProvider.provide(databaseType, userType);
    }

    @SuppressWarnings("unchecked")
    private Field<Object> castField(Field<?> field) {
        return (Field<Object>) field;
    }

    private boolean hasCustomConvertibleTypes() {
        // TODO Check the validity of following statement...
        // jOOQ currently does not use converters for unmapping, provided
        // by a custom ConverterProvider. In that case, the converter has
        // to be invoked manually.
        for (Field<?> field : table.fields()) {
            Class<?> type = field.getDataType().getType();
            if (type == JSON.class || type == YearToSecond.class) {
                return true;
            }
        }
        return false;
    }
}
