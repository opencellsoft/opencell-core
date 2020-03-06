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

package org.meveo.api.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The Class CustomFieldsDto.
 *
 * @author Edward P. Legaspi
 */
@JsonInclude(Include.NON_NULL)
@XmlRootElement(name = "CustomFields")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomFieldsDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 7751924530575980282L;

    /** The custom field. */
    private List<CustomFieldDto> customField;

    /** The inherited custom field. */
    private List<CustomFieldDto> inheritedCustomField;

    /**
     * Instantiates a new custom fields dto.
     */
    public CustomFieldsDto() {

    }

    /**
     * Gets the custom field.
     *
     * @return the custom field
     */
    public List<CustomFieldDto> getCustomField() {
        if (customField == null) {
            customField = new ArrayList<CustomFieldDto>();
        }

        return customField;
    }

    /**
     * Sets the custom field.
     *
     * @param customField the new custom field
     */
    public void setCustomField(List<CustomFieldDto> customField) {
        this.customField = customField;
    }

    /**
     * Gets the cf.
     *
     * @param code the code
     * @return the cf
     */
    public CustomFieldDto getCF(String code) {
        for (CustomFieldDto cf : getCustomField()) {
            if (cf.getCode().equals(code)) {
                return cf;
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CustomFieldsDto [customField=" + customField + "]";
    }

    /**
     * Checks if is empty.
     *
     * @return true, if is empty
     */
    public boolean isEmpty() {
        return (customField == null || customField.isEmpty()) && (inheritedCustomField == null || inheritedCustomField.isEmpty());
    }

    /**
     * Gets the inherited custom field.
     *
     * @return the inherited custom field
     */
    public List<CustomFieldDto> getInheritedCustomField() {
        if (inheritedCustomField == null) {
            inheritedCustomField = new ArrayList<CustomFieldDto>();
        }

        return inheritedCustomField;
    }

    /**
     * Sets the inherited custom field.
     *
     * @param inheritedCustomField the new inherited custom field
     */
    public void setInheritedCustomField(List<CustomFieldDto> inheritedCustomField) {
        this.inheritedCustomField = inheritedCustomField;
    }

}
