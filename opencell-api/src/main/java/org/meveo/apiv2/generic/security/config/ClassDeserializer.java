package org.meveo.apiv2.generic.security.config;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * Json deserializer to deserialize fields with type {@code Class}
 *
 * @author Mounir Boukayoua
 * @since 10.X
 */
public class ClassDeserializer extends JsonDeserializer<Class<?>> {

    @Override
    public Class<?> deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        String className = p.getValueAsString();
        Class<?> aClass;
        try {
            aClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(p, "Class not found for the field: " + className);
        }
        return aClass;
    }
}
