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

package org.meveo.api.dto.catalog;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;

import org.meveo.api.dto.CustomFieldDto;
import org.meveo.model.crm.custom.CustomFieldValue;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The Class ServiceConfigurationDto.
 *
 * @author Edward P. Legaspi
 * @author Said Ramli
 * @lastModifiedVersion 5.1
 */
@XmlRootElement(name = "ServiceConfiguration")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceConfigurationDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 881323828087615069L;

    /** The code. */
    @NotNull
    @XmlAttribute
    private String code;

    /** The description. */
    @XmlAttribute
    private String description;

    /** The custom fields. */
    @XmlElementWrapper(name = "parameters")
    @XmlElement(name = "parameter")
    private List<CustomFieldDto> customFields;

    /**
     * Used in the GUI side only.
     */
    @XmlTransient
    @JsonIgnore
    private Map<String, List<CustomFieldValue>> cfValues;
    
    @XmlTransient
    @JsonIgnore
    private Integer itemIndex;

    /** The mandatory. */
    private boolean mandatory;

    /**
     * Tells us that this service is linked to a BusinessServiceModel.
     */
    private boolean instantiatedFromBSM;

    /**
     * Use when matching service template in bsm vs offer.
     */
    private boolean match = false;
    
    /** The image base64 encoding string. */
    private String imageBase64;
    
    /** The image path. */
    private String imagePath;

    /**
     * Gets the code.
     *
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code.
     *
     * @param code the new code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the custom fields.
     *
     * @return the custom fields
     */
    public List<CustomFieldDto> getCustomFields() {
        return customFields;
    }

    /**
     * Sets the custom fields.
     *
     * @param customFields the new custom fields
     */
    public void setCustomFields(List<CustomFieldDto> customFields) {
        this.customFields = customFields;
    }


    /**
     * Checks if is mandatory.
     *
     * @return true, if is mandatory
     */
    public boolean isMandatory() {
        return mandatory;
    }

    /**
     * Sets the mandatory.
     *
     * @param mandatory the new mandatory
     */
    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    /**
     * Checks if is instantiated from BSM.
     *
     * @return true, if is instantiated from BSM
     */
    public boolean isInstantiatedFromBSM() {
        return instantiatedFromBSM;
    }

    /**
     * Sets the instantiated from BSM.
     *
     * @param instantiatedFromBSM the new instantiated from BSM
     */
    public void setInstantiatedFromBSM(boolean instantiatedFromBSM) {
        this.instantiatedFromBSM = instantiatedFromBSM;
    }

    /**
     * Checks if is match.
     *
     * @return true, if is match
     */
    public boolean isMatch() {
        return match;
    }

    /**
     * Sets the match.
     *
     * @param match the new match
     */
    public void setMatch(boolean match) {
        this.match = match;
    }

    /**
     * Gets the cf values.
     *
     * @return the cf values
     */
    public Map<String, List<CustomFieldValue>> getCfValues() {
        return cfValues;
    }

    /**
     * Sets the cf values.
     *
     * @param cfValues the cf values
     */
    public void setCfValues(Map<String, List<CustomFieldValue>> cfValues) {
        this.cfValues = cfValues;
    }
    
    /**
     * Gets the image Base64 encoding string.
     *
     * @return the image Base64 encoding string
     */
    public String getImageBase64() {
        return imageBase64;
    }
    
    /**
     * Sets the image Base64 encoding string.
     *
     * @param imageBase64 the image Base64 encoding string
     */
    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }
    
    /**
     * Gets the image path.
     *
     * @return the image path
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * Sets the image path.
     *
     * @param imagePath the new image path
     */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * @return the itemIndex
     */
    public Integer getItemIndex() {
        return itemIndex;
    }

    /**
     * @param itemIndex the itemIndex to set
     */
    public void setItemIndex(Integer itemIndex) {
        this.itemIndex = itemIndex;
    }

    @Override
    public String toString() {
        return "ServiceConfigurationDto [code=" + code + ", description=" + description + ", customFields=" + customFields + "]";
    }
}