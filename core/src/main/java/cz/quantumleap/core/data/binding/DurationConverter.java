package cz.quantumleap.core.data.binding;

import org.jetbrains.annotations.NotNull;
import org.jooq.Converter;
import org.jooq.types.YearToSecond;

import java.time.Duration;

public class DurationConverter implements Converter<YearToSecond, Duration> {

    @Override
    public Duration from(YearToSecond databaseObject) {
        return databaseObject != null ? databaseObject.toDuration() : null;
    }

    @Override
    public YearToSecond to(Duration userObject) {
        return YearToSecond.valueOf(userObject);
    }

    @Override
    public @NotNull Class<YearToSecond> fromType() {
        return YearToSecond.class;
    }

    @Override
    public @NotNull Class<Duration> toType() {
        return Duration.class;
    }
}
