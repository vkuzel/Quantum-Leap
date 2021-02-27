package cz.quantumleap.core.database.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.quantumleap.core.database.transport.Lookup;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StringToLookupConverter implements Converter<String, Lookup<?>> {

    private static final Logger log = LoggerFactory.getLogger(StringToLookupConverter.class);

    private final ObjectMapper objectMapper;

    public StringToLookupConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Lookup<?> convert(String source) {
        if (StringUtils.isBlank(source)) {
            return new Lookup<>();
        }

        try {
            return objectMapper.readValue(source, Lookup.class);
        } catch (IOException e) {
            log.warn("String " + source + " cannot be converted to lookup!", e);
            throw new IllegalArgumentException(e);
        }
    }
}
