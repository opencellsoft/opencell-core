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

import java.util.LinkedHashMap;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

/**
 * The Class CustomFieldFormattedValueDto.
 * 
 * @author Abdellatif BARI
 */

@XmlAccessorType(XmlAccessType.FIELD)
public class CustomFieldFormattedValueDto extends BusinessEntityDto {

    /**
     * The auto generated serial no
     */
    private static final long serialVersionUID = 4706817670486265862L;

    /** The long, double or date value. */
    @XmlElement
    protected String singleValue;

    /** The list value. */
    @XmlElement
    protected List<String> listValue;

    /** The map value. */
    @XmlElement
    protected LinkedHashMap<String, String> mapValue;

    /**
     * Instantiates a new custom field dto.
     */
    public CustomFieldFormattedValueDto() {
    }

    /**
     * @return the singleValue
     */
    public String getSingleValue() {
        return singleValue;
    }

    /**
     * @param singleValue the singleValue to set
     */
    public void setSingleValue(String singleValue) {
        this.singleValue = singleValue;
    }

    /**
     * @return the listValue
     */
    public List<String> getListValue() {
        return listValue;
    }

    /**
     * @param listValue the listValue to set
     */
    public void setListValue(List<String> listValue) {
        this.listValue = listValue;
    }

    /**
     * @return the mapValue
     */
    public LinkedHashMap<String, String> getMapValue() {
        return mapValue;
    }

    /**
     * @param mapValue the mapValue to set
     */
    public void setMapValue(LinkedHashMap<String, String> mapValue) {
        this.mapValue = mapValue;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CustomFieldFormattedValueDto [singleValue=" + singleValue + ", listValue=" + listValue + ", mapValue=" + mapValue + "]";
    }

}