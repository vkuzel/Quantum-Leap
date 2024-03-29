package cz.quantumleap.core.view.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import cz.quantumleap.core.database.entity.EntityIdentifier;

import java.io.IOException;

public class EntityIdentifierDeserializer extends JsonDeserializer<EntityIdentifier<?>> {

    @Override
    public EntityIdentifier<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        var valueAsString = p.getValueAsString();
        return EntityIdentifier.parse(valueAsString);
    }
}
