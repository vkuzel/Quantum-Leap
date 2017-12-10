package cz.quantumleap.core.data.converter;

import cz.quantumleap.core.data.transport.EnumValue;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToEnumValueConverter implements Converter<String, EnumValue> {
    @Override
    public EnumValue convert(String source) {
        EnumValue enumValue = new EnumValue();
        if (source != null) {
            enumValue.setId(source);
        }
        return enumValue;
    }
}
