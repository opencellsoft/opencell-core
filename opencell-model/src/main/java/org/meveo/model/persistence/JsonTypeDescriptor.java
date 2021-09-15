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
import java.sql.Clob;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.hibernate.engine.jdbc.CharacterStream;
import org.hibernate.engine.jdbc.internal.CharacterStreamImpl;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.MutableMutabilityPlan;
import org.hibernate.usertype.DynamicParameterizedType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

public class JsonTypeDescriptor extends AbstractTypeDescriptor<Object> implements DynamicParameterizedType {

    private static final long serialVersionUID = -5030465106663645694L;

    private Class<?> jsonObjectClass;

    @Override
    public void setParameterValues(Properties parameters) {
        jsonObjectClass = ((ParameterType) parameters.get(PARAMETER_TYPE)).getReturnedClass();

    }

    @SuppressWarnings("serial")
    public JsonTypeDescriptor() {
        super(Object.class, new MutableMutabilityPlan<Object>() {
            @Override
            protected Object deepCopyNotNull(Object value) {
                return JacksonUtil.clone(value);
            }
        });
    }

    @Override
    public boolean areEqual(Object one, Object another) {
        if (one == another) {
            return true;
        }
        if (one == null || another == null) {
            return false;
        }
        return JacksonUtil.toJsonNode(JacksonUtil.toString(one)).equals(JacksonUtil.toJsonNode(JacksonUtil.toString(another)));
    }

    @Override
    public String toString(Object value) {
        // Logger log = LoggerFactory.getLogger(getClass());
        // log.error("AKK Json converter to string");
        return JacksonUtil.toString(value);
    }

    @Override
    public Object fromString(String string) {

        // Logger log = LoggerFactory.getLogger(getClass());
        // log.error("AKK Json converter from string");

        return JacksonUtil.fromString(string, jsonObjectClass);
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    public <X> X unwrap(Object value, Class<X> type, WrapperOptions options) {
        if (value == null) {
            return null;
        }
//         Logger log = LoggerFactory.getLogger(getClass());
//         log.error("AKK value to unwrap is {}, to a type {}", (value != null ? value.getClass() : null), type);
        if (String.class.isAssignableFrom(type)) {
            return (X) toString(value);

        } else if (CharacterStream.class.isAssignableFrom(type)) {
            return (X) new CharacterStreamImpl(toString(value));

        } else if (JsonNode.class.isAssignableFrom(type)) {
            return (X) JacksonUtil.toJsonNode(toString(value));
        }
        throw unknownUnwrap(type);
    }

    @Override
    public <X> Object wrap(X value, WrapperOptions options) {

        if (value == null) {
            return null;

        } else if (value instanceof Clob) {
            String clobString = null;
            try {
                clobString = IOUtils.toString(((Clob) value).getCharacterStream());

            } catch (IOException | SQLException e) {
                throw new RuntimeException("Failed to read clob value", e);
            }
            return fromString(clobString);

        } else {
            // Logger log = LoggerFactory.getLogger(getClass());
            // log.error("AKKKK value to wrap is " + (value != null ? value.getClass() : null));
            return fromString(value.toString());
        }
    }
}