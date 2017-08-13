package cz.quantumleap.core.data.converter;

import cz.quantumleap.core.data.transport.Lookup;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToLookupConverter implements Converter<String, Lookup> {
    @Override
    public Lookup convert(String source) {
        Lookup lookup = new Lookup();
        if (source != null) {
            lookup.setId(Long.valueOf(source));
        }
        return lookup;
    }
}
