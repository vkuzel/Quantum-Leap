package cz.quantumleap.core.data.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class LocalDateToString implements Converter<LocalDate, String> {
    @Override
    public String convert(LocalDate source) {
        if (source != null) {
            return source.toString();
        }
        return null;
    }
}
