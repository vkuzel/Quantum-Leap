package cz.quantumleap.core.web.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import cz.quantumleap.core.data.entity.EntityIdentifier;

import java.io.IOException;

public class EntityIdentifierSerializer extends JsonSerializer<EntityIdentifier> {

    @Override
    public void serialize(EntityIdentifier entityIdentifier, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(entityIdentifier.toString());
    }
}
