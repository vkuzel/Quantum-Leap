package cz.quantumleap.core.data.converter;

import cz.quantumleap.core.data.transport.Lookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class StringToLookupConverter implements Converter<String, Lookup> {

    private static final Logger log = LoggerFactory.getLogger(StringToLookupConverter.class);

    @Override
    public Lookup convert(String source) {
        Lookup lookup = new Lookup();
        if (!StringUtils.isEmpty(source)) {
            try {
                lookup.setId(Long.valueOf(source));
            } catch (NumberFormatException e) {
                log.warn("String " + source + " cannot be converted to number!", e);
                throw e;
            }
        }
        return lookup;
    }
}
