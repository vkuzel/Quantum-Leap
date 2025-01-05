package cz.quantumleap.core.database.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import static cz.quantumleap.core.utils.Strings.isBlank;

@Component
public class StringToLocalTimeConverter implements Converter<String, LocalTime> {

    private static final Logger log = LoggerFactory.getLogger(StringToLocalTimeConverter.class);

    @Override
    public LocalTime convert(String source) {
        if (isBlank(source)) {
            return null;
        }

        try {
            return LocalTime.parse(source);
        } catch (DateTimeParseException e) {
            log.warn("String " + source + " cannot be converted to a time!", e);
            throw e;
        }
    }
}
