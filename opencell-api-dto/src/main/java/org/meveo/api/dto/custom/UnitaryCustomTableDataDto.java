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

package org.meveo.api.dto.custom;

import java.util.LinkedHashMap;
import java.util.Optional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;

/**
 * Represents data in custom table - custom entity data stored in a separate table
 * This DTO is used for unitary table updates
 * 
 */
@XmlRootElement(name = "CustomTableData")
@XmlAccessorType(XmlAccessType.FIELD)
public class UnitaryCustomTableDataDto extends BaseEntityDto {

    private static final long serialVersionUID = -1209601309024979414L;

    /**
     * Custom table/custom entity template code
     */
    @XmlAttribute(required = true)
    private String customTableCode;

    /**
     * Should data be overwritten (deleted all data first) instead of appended to existing values. Defaults to false if omitted.
     */
    @XmlAttribute
    private Boolean overwrite;

    /**
     * A list of values with field name as map's key and field value as map's value
     */
    @XmlAttribute(name = "record", required = true)
    private CustomTableRecordDto value;

    /**
     * @return Custom table/custom entity template code
     */
    public String getCustomTableCode() {
        return customTableCode;
    }

    /**
     * @param customTableCode Custom table/custom entity template code
     */
    public void setCustomTableCode(String customTableCode) {
        this.customTableCode = customTableCode;
    }

    /**
     * @return Should data be overwritten (deleted all data first) instead of appended to existing values. Defaults to false if null.
     */
    public Boolean getOverwrite() {
        return Optional.ofNullable(overwrite).orElse(false);
    }

    /**
     * @param overrwrite Should data be overwritten (deleted all data first) instead of appended to existing values.
     */
    public void setOverwrite(Boolean overrwrite) {
        this.overwrite = overrwrite;
    }

    /**
     * @return value with field name as map's key and field value as map's value
     */
    public CustomTableRecordDto getValue() {
        return value;
    }

    public LinkedHashMap<String, Object> getRowValues() {
        return Optional.ofNullable(value).map(CustomTableRecordDto::getValues).orElse(new LinkedHashMap<>());
    }

    /**
     * @param value with field name as map's key and field value as map's value
     */
    public void setValue(CustomTableRecordDto value) {
        this.value = value;
    }

}
