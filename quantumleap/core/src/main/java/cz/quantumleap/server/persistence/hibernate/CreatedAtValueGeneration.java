package cz.quantumleap.server.persistence.hibernate;

import org.hibernate.tuple.AnnotationValueGeneration;
import org.hibernate.tuple.GenerationTiming;
import org.hibernate.tuple.ValueGenerator;

import java.time.LocalDateTime;

public class CreatedAtValueGeneration implements AnnotationValueGeneration<CreatedAt> {
    @Override
    public void initialize(CreatedAt annotation, Class<?> propertyType) {
        if (!LocalDateTime.class.isAssignableFrom(propertyType)) {
            throw new IllegalArgumentException("Type " + propertyType.getName() + " is not supported! See CreationTimestampGeneration.");
        }
    }

    @Override
    public GenerationTiming getGenerationTiming() {
        return GenerationTiming.INSERT;
    }

    @Override
    public ValueGenerator<?> getValueGenerator() {
        return (ValueGenerator<LocalDateTime>) (session, owner) -> LocalDateTime.now();
    }

    @Override
    public boolean referenceColumnInSql() {
        return false;
    }

    @Override
    public String getDatabaseGeneratedReferencedColumnValue() {
        return null;
    }
}
