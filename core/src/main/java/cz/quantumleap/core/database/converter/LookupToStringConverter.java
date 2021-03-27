package cz.quantumleap.core.database.converter;

import cz.quantumleap.core.database.domain.Slice;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class LookupToStringConverter implements Converter<Slice.Lookup, String> {
    @Override
    public String convert(Slice.Lookup source) {
        return source.getLabel();
    }
}
