package cz.quantumleap.core.data.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.DateTimeException;
import java.time.LocalDateTime;

@Component
public class StringToLocalDateTimeConverter implements Converter<String, LocalDateTime> {

    private static final Logger log = LoggerFactory.getLogger(StringToLocalDateTimeConverter.class);

    @Override
    public LocalDateTime convert(String source) {
        if (StringUtils.isEmpty(source)) {
            return null;
        }

        try {
            return LocalDateTime.parse(source);
        } catch (DateTimeException e) {
            log.warn("String " + source + " cannot be converted to a date-time!", e);
            throw e;
        }
    }
}