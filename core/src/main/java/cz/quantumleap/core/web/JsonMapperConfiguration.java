package cz.quantumleap.core.web;

import cz.quantumleap.core.data.entity.EntityIdentifier;
import cz.quantumleap.core.web.serializer.EntityIdentifierDeserializer;
import cz.quantumleap.core.web.serializer.EntityIdentifierSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JsonMapperConfiguration implements Jackson2ObjectMapperBuilderCustomizer {

    @Override
    public void customize(Jackson2ObjectMapperBuilder builder) {
        builder.serializerByType(EntityIdentifier.class, new EntityIdentifierSerializer());
        builder.deserializerByType(EntityIdentifier.class, new EntityIdentifierDeserializer());
    }
}