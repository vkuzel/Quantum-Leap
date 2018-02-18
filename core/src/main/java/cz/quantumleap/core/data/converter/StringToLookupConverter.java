package cz.quantumleap.core.data.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.quantumleap.core.data.transport.Lookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Component
public class StringToLookupConverter implements Converter<String, Lookup> {

    private static final Logger log = LoggerFactory.getLogger(StringToLookupConverter.class);

    private final ObjectMapper objectMapper;

    public StringToLookupConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Lookup convert(String source) {
        if (StringUtils.isEmpty(source)) {
            return new Lookup();
        }

        try {
            return objectMapper.readValue(source, Lookup.class);
        } catch (IOException e) {
            log.warn("String " + source + " cannot be converted to lookup!", e);
            throw new IllegalArgumentException(e);
        }
    }
}
