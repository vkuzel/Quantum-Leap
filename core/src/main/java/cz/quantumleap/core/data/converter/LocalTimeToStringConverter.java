package cz.quantumleap.core.data.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class LocalTimeToStringConverter implements Converter<LocalTime, String> {
    @Override
    public String convert(LocalTime source) {
        if (source != null) {
            return source.toString();
        }
        return null;
    }
}
