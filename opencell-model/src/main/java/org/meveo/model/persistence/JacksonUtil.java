package org.meveo.model.persistence;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JacksonUtil {

    static {
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(om.getVisibilityChecker().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
        om.setVisibility(om.getVisibilityChecker().withGetterVisibility(JsonAutoDetect.Visibility.NONE));
        om.setVisibility(om.getVisibilityChecker().withIsGetterVisibility(Visibility.NONE));
        om.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        om.setSerializationInclusion(Include.NON_NULL);
        OBJECT_MAPPER = om;
    }

    public static ObjectMapper OBJECT_MAPPER;

    public static <T> T fromString(String string, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(string, clazz);
        } catch (IOException e) {
            throw new IllegalArgumentException("The given string value: " + string + " cannot be transformed to Json object", e);
        }
    }

    public static <T> T fromString(String string, TypeReference<T> typeReference) {
        try {
            return OBJECT_MAPPER.readValue(string, typeReference);
        } catch (IOException e) {
            throw new IllegalArgumentException("The given string value: " + string + " cannot be transformed to Json object", e);
        }
    }

    public static String toString(Object value) {
        try {
            return OBJECT_MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("The given Json object value: " + value + " cannot be transformed to a String", e);
        }
    }

    public static JsonNode toJsonNode(String value) {
        try {
            return OBJECT_MAPPER.readTree(value);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T clone(T value) {
        return fromString(toString(value), (Class<T>) value.getClass());
    }
}