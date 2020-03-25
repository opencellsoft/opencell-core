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

package org.meveo.model.jaxb.customer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.meveo.model.crm.custom.CustomFieldValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "customField" })
@XmlRootElement(name = "customFields")
public class CustomFields {
    protected List<CustomField> customField;

    public CustomFields() {

    }

    public static CustomFields toDTO(Map<String, List<CustomFieldValue>> customFields) {
        if (customFields == null || customFields.isEmpty()) {
            return null;
        }
        CustomFields dto = new CustomFields();
        for (Entry<String, List<CustomFieldValue>> cfValueInfo : customFields.entrySet()) {
            String cfCode = cfValueInfo.getKey();
            for (CustomFieldValue cfValue : cfValueInfo.getValue()) {
                dto.getCustomField().add(CustomField.toDTO(cfCode, cfValue));
            }
        }
        return dto;
    }

    public List<CustomField> getCustomField() {
        if (customField == null) {
            customField = new ArrayList<CustomField>();
        }

        return customField;
    }

    public void setCustomField(List<CustomField> customField) {
        this.customField = customField;
    }

    public CustomField getCF(String code) {
        for (CustomField cf : getCustomField()) {
            if (cf.getCode().equals(code)) {
                return cf;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "CustomFields [customField=" + customField + "]";
    }
}
