package cz.quantumleap.core.database.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static cz.quantumleap.core.utils.Strings.isBlank;

@Component
public class StringToLocalDateConverter implements Converter<String, LocalDate> {

    private static final Logger log = LoggerFactory.getLogger(StringToLocalDateConverter.class);

    @Override
    public LocalDate convert(String source) {
        if (isBlank(source)) {
            return null;
        }

        try {
            return LocalDate.parse(source);
        } catch (NumberFormatException e) {
            log.warn("String " + source + " cannot be converted to a date!", e);
            throw e;
        }
    }
}
