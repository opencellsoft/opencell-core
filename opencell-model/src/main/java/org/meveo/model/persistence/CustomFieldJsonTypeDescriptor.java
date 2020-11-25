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

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;
import org.meveo.commons.encryption.IEncryptable;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.crm.custom.CustomFieldValue;
import org.meveo.model.crm.custom.CustomFieldValues;

import com.fasterxml.jackson.core.type.TypeReference;

public class CustomFieldJsonTypeDescriptor extends AbstractTypeDescriptor<CustomFieldValues> implements IEncryptable {

    private static final long serialVersionUID = -5030465106663645694L;

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

        if (TRUE_STR.equalsIgnoreCase(ParamBean.getInstance().getProperty(ENCRYPT_CUSTOM_FIELDS_PROPERTY, FALSE_STR)) && value.isEncrypted()) {
            return encrypt(((CustomFieldValues) value).asJson());
        }

        return ((CustomFieldValues) value).asJson();

    }

    @Override
    public CustomFieldValues fromString(String string) {

        if (StringUtils.isBlank(string)) {
            return null;
        }

        if (TRUE_STR.equalsIgnoreCase(ParamBean.getInstance().getProperty(ENCRYPT_CUSTOM_FIELDS_PROPERTY, FALSE_STR))) {
            string = decrypt(string);
        }

        Map<String, List<CustomFieldValue>> cfValues = JacksonUtil.fromString(string, new TypeReference<Map<String, List<CustomFieldValue>>>() {
        });

        return new CustomFieldValues(cfValues);
    }

    @Override
    public <X> X unwrap(CustomFieldValues value, Class<X> type, WrapperOptions options) {

        if (value == null) {
            return null;
        } else if (String.class.isAssignableFrom(type)) {
            return (X) toString(value);
        }
        throw unknownUnwrap(type);
    }

    @Override
    public <X> CustomFieldValues wrap(X value, WrapperOptions options) {

        if (value == null) {
            return null;
        } else if (String.class.isInstance(value)) {
            return fromString((String) value);
        }
        throw unknownWrap(value.getClass());
    }

    @Override
    public boolean areEqual(CustomFieldValues one, CustomFieldValues another) {
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