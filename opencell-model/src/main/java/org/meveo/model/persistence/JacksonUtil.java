/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.model.persistence;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JacksonUtil {

    private static final ThreadLocal<ObjectMapper> OBJECT_MAPPER = new ThreadLocal<ObjectMapper>() {
        @Override
        protected ObjectMapper initialValue() {
            ObjectMapper om = new ObjectMapper();
            om.setVisibility(om.getVisibilityChecker().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
            om.setVisibility(om.getVisibilityChecker().withGetterVisibility(JsonAutoDetect.Visibility.NONE));
            om.setVisibility(om.getVisibilityChecker().withIsGetterVisibility(Visibility.NONE));
            om.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
            om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

            om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            om.setSerializationInclusion(Include.NON_NULL);

            return om;
        }
    };

    public static <T> T fromString(String string, Class<T> clazz) {
        try {
            ObjectMapper om = OBJECT_MAPPER.get();
            return om.readValue(string, clazz);

        } catch (IOException e) {
            throw new IllegalArgumentException("The given string value: " + string + " cannot be transformed to Json object", e);
        }
    }

    public static <T> T fromString(String string, TypeReference<T> typeReference) {
        try {
            ObjectMapper om = OBJECT_MAPPER.get();
            return om.readValue(string, typeReference);

        } catch (IOException e) {
            throw new IllegalArgumentException("The given string value: " + string + " cannot be transformed to Json object", e);
        }
    }

    public static String toString(Object value) {
        try {
            ObjectMapper om = OBJECT_MAPPER.get();
            return om.writeValueAsString(value);

        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("The given Json object value: " + value + " cannot be transformed to a String", e);
        }
    }

    public static JsonNode toJsonNode(String value) {
        try {
            ObjectMapper om = OBJECT_MAPPER.get();
            return om.readTree(value);

        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static JsonNode toJsonNode(Object value) {
        ObjectMapper om = OBJECT_MAPPER.get();
        return om.valueToTree(value);
    }

    @SuppressWarnings("unchecked")
    public static <T> T clone(T value) {
        return fromString(toString(value), (Class<T>) value.getClass());
    }
}