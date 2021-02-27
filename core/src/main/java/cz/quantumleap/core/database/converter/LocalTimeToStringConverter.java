package cz.quantumleap.core.database.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
@SuppressWarnings("unused")
public class LocalTimeToStringConverter implements Converter<LocalTime, String> {
    @Override
    public String convert(LocalTime source) {
        return source.toString();
    }
}
