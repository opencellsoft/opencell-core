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

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.IEntityDto;
import org.meveo.commons.utils.StringUtils;

/**
 * Represents data in custom table - custom entity data stored in a separate table
 * 
 * @author Andrius Karpavicius
 */
@XmlRootElement(name = "CustomTableRecord")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomTableRecordDto implements Serializable, IEntityDto {

    private static final long serialVersionUID = -1209601309024979418L;
    private static final String ID_KEY = "id";
    
    private Long id;

    private LinkedHashMap<String, Object> values;
    
    private String tableName;
    
    private String display;

    public CustomTableRecordDto() {

    }
    
    public CustomTableRecordDto(Map<String, Object> values, String tableName) {
    	this(values);
    	this.tableName=tableName;
    }
    
    public CustomTableRecordDto(String display, Map<String, Object> values) {
    	this(values);
    	this.display=display;
    }

    public CustomTableRecordDto(Map<String, Object> values) {
        this.values = new LinkedHashMap<>(values);
        this.id = Long.valueOf(values.getOrDefault(ID_KEY, 0L).toString());
    }

    /**
     * @return A list of values with field name as map's key and field value as map's value
     */
    public LinkedHashMap<String, Object> getValues() {
        return values;
    }

    /**
     * @param values A list of values with field name as map's key and field value as map's value
     */
    public void setValues(LinkedHashMap<String, Object> values) {
        this.values = values;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
	public String display() {
		if(display!=null) {
			return display;
		}
		String tName = StringUtils.isBlank(tableName) ? "" : tableName + " ";
		return tName + values.toString();
	}

	/**
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * @param tableName the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
}