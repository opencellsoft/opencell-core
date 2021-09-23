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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.engine.jdbc.CharacterStream;
import org.hibernate.engine.jdbc.internal.CharacterStreamImpl;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;
import org.meveo.commons.encryption.IEncryptable;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.crm.custom.CustomFieldValue;
import org.meveo.model.crm.custom.CustomFieldValues;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

public class CustomFieldJsonTypeDescriptor extends AbstractTypeDescriptor<CustomFieldValues> implements IEncryptable {

    private static final long serialVersionUID = -5030465106663645694L;

    private static boolean ENCRYPT_CF = TRUE_STR.equalsIgnoreCase(ParamBean.getInstance().getProperty(ENCRYPT_CUSTOM_FIELDS_PROPERTY, FALSE_STR));

    public static final CustomFieldJsonTypeDescriptor INSTANCE = new CustomFieldJsonTypeDescriptor();

    @SuppressWarnings("unchecked")
    public CustomFieldJsonTypeDescriptor() {

        super(CustomFieldValues.class, ImmutableMutabilityPlan.INSTANCE);

    }

    @Override
    public String toString(CustomFieldValues value) {

        if (value == null) {
            return null;
        }

        if (ENCRYPT_CF) {
            return encrypt(value.asJson());
        }

        return value.asJson();
    }

    @Override
    public CustomFieldValues fromString(String string) {

        if (StringUtils.isBlank(string)) {
            return null;
        }

        if (ENCRYPT_CF) {
            string = decrypt(string);
            if (IEncryptable.ON_ERROR_RETURN.equalsIgnoreCase(string)) {
                return null;
            }
        }

        if (!ENCRYPT_CF && string.startsWith(ENCRYPTION_CHECK_STRING)) {
            Map<String, List<CustomFieldValue>> cfValues = new HashMap<String, List<CustomFieldValue>>();
            cfValues.put("AES", new ArrayList<CustomFieldValue>());
            return new CustomFieldValues(cfValues);
        }

        Map<String, List<CustomFieldValue>> cfValues = JacksonUtil.fromString(string, new TypeReference<Map<String, List<CustomFieldValue>>>() {
        });

        return new CustomFieldValues(cfValues);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <X> X unwrap(CustomFieldValues value, Class<X> type, WrapperOptions options) {

//        Logger log = LoggerFactory.getLogger(getClass());
//        log.error("AKK CF value to unwrap is {}, to a type {}", (value != null ? value.getClass() : null), type);

        if (value == null) {
            return null;

        } else if (CharacterStream.class.isAssignableFrom(type)) {
            return (X) new CharacterStreamImpl(toString(value));

        } else if (String.class.isAssignableFrom(type)) {
            return (X) toString(value);

        } else if (JsonNode.class.isAssignableFrom(type)) {
            return (X) JacksonUtil.toJsonNode(toString(value));
        }
        
        throw unknownUnwrap(type);
    }

    @Override
    public <X> CustomFieldValues wrap(X value, WrapperOptions options) {

//        Logger log = LoggerFactory.getLogger(getClass());
//        log.error("AKKKK CF value to wrap is " + (value != null ? value.getClass() : null));

        if (value == null) {
            return null;

        } else if (String.class.isInstance(value)) {
            return fromString((String) value);

            // Support for Oracle's CLOB type field
        } else if (value instanceof Clob) {
            String clobString = null;
            try {
                clobString = IOUtils.toString(((Clob) value).getCharacterStream());

            } catch (IOException | SQLException e) {
                throw new RuntimeException("Failed to read clob value", e);
            }
            return fromString(clobString);
            
            // Support for Postgresql JsonB type field
        } else {
            // Logger log = LoggerFactory.getLogger(getClass());
            // log.error("AKKKK value to wrap is " + (value != null ? value.getClass() : null));
            return fromString(value.toString());
        }
    }

    @Override
    public boolean areEqual(CustomFieldValues one, CustomFieldValues another) {
        
//        Logger log = LoggerFactory.getLogger(getClass());
//        log.error("AKKKK CF value is equal check");

        boolean equals = super.areEqual(one, another);

        if (equals && one != null && CollectionUtils.isNotEmpty(one.getDirtyCfValues())) {
            if (another != null && CollectionUtils.isNotEmpty(another.getDirtyCfValues())) {

                if (!CollectionUtils.isEqualCollection(one.getDirtyCfValues(), another.getDirtyCfValues())) {
                    return false;
                } else {
                    return true;
                }

            } else {
                return false;
            }
        }
        return equals;
    }
}