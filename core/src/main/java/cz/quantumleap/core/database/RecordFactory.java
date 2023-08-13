package cz.quantumleap.core.database;

import cz.quantumleap.core.utils.ReflectionUtils;
import org.jooq.Record;
import org.jooq.*;
import org.jooq.types.YearToSecond;

import java.util.Set;
import java.util.stream.Collectors;

import static cz.quantumleap.core.utils.Strings.lowerUnderscoreToLowerCamel;

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
        var record = dslContext.newRecord(table);
        if (customConvertibleTypes) {
            for (var field : record.fields()) {
                setValueToRecordField(transport, field, record);
            }
        } else {
            record.from(transport);
        }
        return record;
    }

    private void setValueToRecordField(T transport, Field<?> field, Record record) {
        var transportFieldName = lowerUnderscoreToLowerCamel(field.getName().toLowerCase());
        if (transportFieldNames.contains(transportFieldName)) {
            var databaseType = field.getDataType();
            var value = ReflectionUtils.getClassFieldValue(transportType, transport, transportFieldName);
            if (value != null) {
                var userType = value.getClass();
                var converter = resolveConverter(databaseType, userType);
                record.setValue(castField(field), value, converter);
            } else if (databaseType.nullable()) {
                record.setValue(castField(field), null);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Converter<Object, Object> resolveConverter(DataType<?> dataType, Class<?> userType) {
        var databaseType = dataType.getType();
        return (Converter<Object, Object>) converterProvider.provide(databaseType, userType);
    }

    @SuppressWarnings("unchecked")
    private Field<Object> castField(Field<?> field) {
        return (Field<Object>) field;
    }

    private boolean hasCustomConvertibleTypes() {
        // jOOQ currently does not use converters in the DefaultRecordUnmapper
        // for unmapping, provided by a custom ConverterProvider. In that case,
        // the converter has to be invoked manually.
        for (var field : table.fields()) {
            var type = field.getDataType().getType();
            if (type == JSON.class || type == YearToSecond.class) {
                return true;
            }
        }
        return false;
    }
}
