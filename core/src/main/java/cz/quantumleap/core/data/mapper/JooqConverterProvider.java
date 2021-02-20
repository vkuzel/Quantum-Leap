package cz.quantumleap.core.data.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.quantumleap.core.data.entity.EntityIdentifier;
import org.jetbrains.annotations.Nullable;
import org.jooq.Converter;
import org.jooq.ConverterProvider;
import org.jooq.JSON;
import org.jooq.types.YearToSecond;

import java.time.Duration;

public class JooqConverterProvider implements ConverterProvider { // TODO Move elsewhere...

    private final ConverterProvider delegate;
    private final ObjectMapper objectMapper;

    public JooqConverterProvider(ConverterProvider delegate, ObjectMapper objectMapper) {
        this.delegate = delegate;
        this.objectMapper = objectMapper;
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable <T, U> Converter<T, U> provide(Class<T> tType, Class<U> uType) {
        if (tType == JSON.class) {
            return Converter.ofNullable(
                    tType, uType,
                    t -> convertJsonToUserType((JSON) t, uType),
                    u -> (T) convertValueToJson(u)
            );
        } else if (tType == YearToSecond.class && uType == Duration.class) {
            return Converter.ofNullable(
                    tType, uType,
                    t -> (U) ((YearToSecond) t).toDuration(),
                    u -> (T) YearToSecond.valueOf((Duration) u)
            );
        } else if (tType == String.class && uType == EntityIdentifier.class) {
            return Converter.ofNullable(
                    tType, uType,
                    t -> (U) EntityIdentifier.parse((String) t),
                    u -> (T) u.toString()
            );
        } else {
            return delegate.provide(tType, uType);
        }
    }

    private <U> U convertJsonToUserType(JSON json, Class<U> userType) {
        try {
            return objectMapper.readValue("" + json, userType);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    private <T> JSON convertValueToJson(T value) {
        try {
            String jsonString = objectMapper.writeValueAsString(value);
            return JSON.valueOf(jsonString);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }
}
