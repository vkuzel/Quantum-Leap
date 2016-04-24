package cz.quantumleap.server.persistence.hibernate;

import org.hibernate.annotations.ValueGenerationType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@ValueGenerationType(generatedBy = UpdatedAtValueGeneration.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface UpdatedAt {
}
