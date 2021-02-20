package cz.quantumleap.core.data.converter;

import cz.quantumleap.core.data.transport.TableSlice;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class LookupToStringConverter implements Converter<TableSlice.Lookup, String> {
    @Override
    public String convert(TableSlice.Lookup source) {
        return source.getLabel();
    }
}
