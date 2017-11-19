package cz.quantumleap.core.data.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class LocalDateTimeToString implements Converter<LocalDateTime, String> {
    @Override
    public String convert(LocalDateTime source) {
        if (source != null) {
            return source.toString();
        }
        return null;
    }
}
