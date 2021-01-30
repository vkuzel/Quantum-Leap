package cz.quantumleap.core.data.converter;

import cz.quantumleap.core.data.transport.SetValues;
import cz.quantumleap.core.data.transport.SetValues.Value;
import cz.quantumleap.core.data.transport.SetValues.ValueSet;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringsToSetValuesConverter implements Converter<String[], SetValues> {

    @Override
    public SetValues convert(String[] source) {
        ValueSet values = new ValueSet();
        for (String token : source) {
            if (StringUtils.isNotBlank(token)) {
                values.add(new Value(token, token));
            }
        }
        SetValues setValues = new SetValues();
        setValues.setValues(values);
        return setValues;
    }
}
