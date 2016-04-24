package cz.quantumleap.server.persistence.hibernate;

import org.hibernate.tuple.AnnotationValueGeneration;
import org.hibernate.tuple.GenerationTiming;
import org.hibernate.tuple.ValueGenerator;

public class UpdatedByValueGeneration implements AnnotationValueGeneration<UpdatedBy> {
    @Override
    public void initialize(UpdatedBy annotation, Class<?> propertyType) {
        if (!Long.TYPE.isAssignableFrom(propertyType) && !Long.class.isAssignableFrom(propertyType)) {
            throw new IllegalArgumentException("Type " + propertyType.getName() + " is not supported!");
        }
    }

    @Override
    public GenerationTiming getGenerationTiming() {
        return GenerationTiming.ALWAYS;
    }

    @Override
    public ValueGenerator<?> getValueGenerator() {
        return (ValueGenerator<Long>) (session, owner) -> {
            return 0L; // TODO Implement this when user services are going to be available http://docs.jboss.org/hibernate/orm/4.3/topical/html/generated/GeneratedValues.html#in-memory-example2
        };
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
