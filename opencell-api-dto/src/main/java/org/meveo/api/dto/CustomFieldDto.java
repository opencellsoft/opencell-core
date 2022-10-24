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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.meveo.model.crm.custom.CustomFieldIndexTypeEnum;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;



/**
 * The Class CustomFieldDto.
 * 
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
*/

@XmlRootElement(name = "CustomField")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonInclude(Include.NON_NULL)
public class CustomFieldDto {

    /** The code. */
    @XmlAttribute(required = true)
    protected String code;
    
    /** The description. */
    @XmlAttribute(required = true)
    protected String description;
    
    /** The field type. */
    protected CustomFieldTypeEnum fieldType;
    
    /** The language descriptions. */
    protected List<LanguageDescriptionDto> languageDescriptions;

    /** The value date. */
    @XmlAttribute
    protected Date valueDate;

    /** The value period start date. */
    @XmlAttribute
    protected Date valuePeriodStartDate;

    /** The value period end date. */
    @XmlAttribute
    protected Date valuePeriodEndDate;

    /** The value period priority. */
    @XmlAttribute
    protected Integer valuePeriodPriority;

    /** The string value. */
    @XmlElement
    protected String stringValue;

    /** The date value. */
    @XmlElement
    protected Date dateValue;

    /** The long value. */
    @XmlElement
    protected Long longValue;

    /** The double value. */
    @XmlElement
    protected Double doubleValue;
    
    /** The boolean value. */
    @XmlElement
    protected Boolean booleanValue;

    /** The list value. */
    @XmlElementWrapper(name = "listValue")
    @XmlElement(name = "value")
    @JsonProperty("value")
    protected List<CustomFieldValueDto> listValue;

    /** The map value. */
    // DO NOT change to Map. Used LinkedHashMap to preserve the item order during read/write
    @XmlElement
    protected LinkedHashMap<String, CustomFieldValueDto> mapValue;

    /** The entity reference value. */
    @XmlElement()
    protected EntityReferenceDto entityReferenceValue;

    /** The value converted. */
    // A transient object. Contains a converted value from DTO to some object when it is applicable
    @XmlTransient
    protected Object valueConverted;
   

    /** The index type. */
    @XmlElement()
    private CustomFieldIndexTypeEnum indexType;
    
    /** The file value. */
    @XmlElement
    protected String fileValue;

    /** The formatted value. */
    @XmlElement()
    protected CustomFieldFormattedValueDto formattedValue;

    /** The Url reference value. */
    @XmlElement() protected UrlReferenceDto urlReferenceValue;

    /**
     * Where field should be displayed. Format: tab:&lt;tab name&gt;:&lt;tab relative position&gt;;fieldGroup:&lt;fieldgroup name&gt;:&lt;fieldgroup relative
     * position&gt;;field:&lt;field relative position in fieldgroup/tab&gt;
     * <p>
     * Tab and field group names support translation in the following format: &lt;default value&gt;|&lt;language3 letter key=translated value&gt;
     * <p>
     * e.g. tab:Tab default title|FRA=Title in french|ENG=Title in english:0;fieldGroup:Field group default label|FRA=Field group label in french|ENG=Field group label in
     * english:0;field:0 OR tab:Second tab:1;field:1
     */
    protected String guiPosition;

    /**
     * Custom Table Code.
     */
    private String customTableCode;
    /**
     * Filters for custom table wrapper.
     */
    private String dataFilter;

    /**
     * Fields for cutsom table wrapper.
     */
    private String fields;

    /**
     * Instantiates a new custom field dto.
     */
    public CustomFieldDto() {
    }

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
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the field type.
     *
     * @return the fieldType
     */
    public CustomFieldTypeEnum getFieldType() {
        return fieldType;
    }

    /**
     * Sets the field type.
     *
     * @param fieldType the fieldType to set
     */
    public void setFieldType(CustomFieldTypeEnum fieldType) {
        this.fieldType = fieldType;
    }
    
    

    /**
     * Gets the language descriptions.
     *
     * @return the languageDescriptions
     */
    public List<LanguageDescriptionDto> getLanguageDescriptions() {
        return languageDescriptions;
    }

    /**
     * Sets the language descriptions.
     *
     * @param languageDescriptions the languageDescriptions to set
     */
    public void setLanguageDescriptions(List<LanguageDescriptionDto> languageDescriptions) {
        this.languageDescriptions = languageDescriptions;
    }

    /**
     * Gets the string value.
     *
     * @return the string value
     */
    public String getStringValue() {
        return stringValue;
    }

    /**
     * Sets the string value.
     *
     * @param stringValue the new string value
     */
    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    /**
     * Gets the date value.
     *
     * @return the date value
     */
    public Date getDateValue() {
        return dateValue;
    }

    /**
     * Sets the date value.
     *
     * @param dateValue the new date value
     */
    public void setDateValue(Date dateValue) {
        this.dateValue = dateValue;
    }

    /**
     * Gets the long value.
     *
     * @return the long value
     */
    public Long getLongValue() {
        return longValue;
    }

    /**
     * Sets the long value.
     *
     * @param longValue the new long value
     */
    public void setLongValue(Long longValue) {
        this.longValue = longValue;
    }

    /**
     * Gets the double value.
     *
     * @return the double value
     */
    public Double getDoubleValue() {
        return doubleValue;
    }

    /**
     * Sets the double value.
     *
     * @param doubleValue the new double value
     */
    public void setDoubleValue(Double doubleValue) {
        this.doubleValue = doubleValue;
    }

    /**
     * Gets the boolean value.
     * 
     * @return the boolean value
     */
	public Boolean getBooleanValue() {
		return booleanValue;
	}

	/**
	 * Sets the boolean value.
	 * 
	 * @param booleanValue the boolean value
	 */
	public void setBooleanValue(Boolean booleanValue) {
		this.booleanValue = booleanValue;
	}

    /**
     * Gets the value date.
     *
     * @return the value date
     */
    public Date getValueDate() {
        return valueDate;
    }

    /**
     * Sets the value date.
     *
     * @param valueDate the new value date
     */
    public void setValueDate(Date valueDate) {
        this.valueDate = valueDate;
    }

    /**
     * Gets the value period start date.
     *
     * @return the value period start date
     */
    public Date getValuePeriodStartDate() {
        return valuePeriodStartDate;
    }

    /**
     * Sets the value period start date.
     *
     * @param valuePeriodStartDate the new value period start date
     */
    public void setValuePeriodStartDate(Date valuePeriodStartDate) {
        this.valuePeriodStartDate = valuePeriodStartDate;
    }

    /**
     * Gets the value period end date.
     *
     * @return the value period end date
     */
    public Date getValuePeriodEndDate() {
        return valuePeriodEndDate;
    }

    /**
     * Sets the value period end date.
     *
     * @param valuePeriodEndDate the new value period end date
     */
    public void setValuePeriodEndDate(Date valuePeriodEndDate) {
        this.valuePeriodEndDate = valuePeriodEndDate;
    }

    /**
     * Sets the value period priority.
     *
     * @param valuePeriodPriority the new value period priority
     */
    public void setValuePeriodPriority(Integer valuePeriodPriority) {
        this.valuePeriodPriority = valuePeriodPriority;
    }

    /**
     * Gets the value period priority.
     *
     * @return the value period priority
     */
    public Integer getValuePeriodPriority() {
        return valuePeriodPriority;
    }

    /**
     * Gets the list value.
     *
     * @return the list value
     */
    public List<CustomFieldValueDto> getListValue() {
        return listValue;
    }

    /**
     * Sets the list value.
     *
     * @param listValue the new list value
     */
    public void setListValue(List<CustomFieldValueDto> listValue) {
        this.listValue = listValue;
    }

    /**
     * Gets the map value.
     *
     * @return the map value
     */
    public Map<String, CustomFieldValueDto> getMapValue() {
        return mapValue;
    }

    /**
     * Sets the map value.
     *
     * @param mapValue the map value
     */
    public void setMapValue(LinkedHashMap<String, CustomFieldValueDto> mapValue) {
        this.mapValue = mapValue;
    }

    /**
     * Gets the entity reference value.
     *
     * @return the entity reference value
     */
    public EntityReferenceDto getEntityReferenceValue() {
        return entityReferenceValue;
    }

    /**
     * Sets the entity reference value.
     *
     * @param entityReferenceValue the new entity reference value
     */
    public void setEntityReferenceValue(EntityReferenceDto entityReferenceValue) {
        this.entityReferenceValue = entityReferenceValue;
    }

    /**
     * Check if value is empty given specific field or storage type.
     *
     * @param fieldType Field type to check
     * @param storageType Storage type to check
     * @return True if value is empty
     */
    public boolean isEmpty(CustomFieldTypeEnum fieldType, CustomFieldStorageTypeEnum storageType) {
        if (storageType == CustomFieldStorageTypeEnum.MAP || storageType == CustomFieldStorageTypeEnum.MATRIX) {
            if (mapValue == null || mapValue.isEmpty()) {
                return true;
            }

            for (Entry<String, CustomFieldValueDto> mapItem : mapValue.entrySet()) {
                if (mapItem.getKey() == null || mapItem.getKey().isEmpty() || mapItem.getValue() == null || mapItem.getValue().isEmpty()) {
                    return true;
                }
            }

        } else if (storageType == CustomFieldStorageTypeEnum.LIST) {
            if (listValue == null || listValue.isEmpty()) {
                return true;
            }

            for (CustomFieldValueDto listItem : listValue) {
                if (listItem == null || listItem.isEmpty()) {
                    return true;
                }
            }

        } else if (storageType == CustomFieldStorageTypeEnum.SINGLE) {
            switch (fieldType) {
            case DATE:
                return dateValue == null;
            case DOUBLE:
                return doubleValue == null;
            case BOOLEAN:
            	return booleanValue == null;
            case LONG:
                return longValue == null;
            case LIST:
            case STRING:
            case TEXT_AREA:
                return stringValue == null;
            case ENTITY:
                return entityReferenceValue == null || entityReferenceValue.isEmpty();
            case CHILD_ENTITY:
                return true; // TODO add implementation for child entity value
                case URL:
                    return urlReferenceValue == null || urlReferenceValue.isEmpty();
            }
        }
        return false;
    }

    /**
     * A generic way to check if value is empty.
     *
     * @return True if value is empty
     */
    public boolean isEmpty() {
        // TODO add implementation for child entity value

        if (mapValue != null) {
            for (Entry<String, CustomFieldValueDto> mapItem : mapValue.entrySet()) {
                if (mapItem.getKey() != null && !mapItem.getKey().isEmpty() && mapItem.getValue() != null && mapItem.getValue().isEmpty()) {
                    return false;
                }
            }
        }
        if (listValue != null) {
            for (CustomFieldValueDto listItem : listValue) {
                if (listItem != null && !listItem.isEmpty()) {
                    return false;
                }
            }
        }
        if (dateValue != null) {
            return false;
        } else if (doubleValue != null) {
            return false;
        } else if (booleanValue != null) {
            return false;
        } else if (longValue != null) {
            return false;
        } else if (stringValue != null && !stringValue.isEmpty()) {
            return false;
        } else if (entityReferenceValue == null || !entityReferenceValue.isEmpty()) {
            return false;
        }
        else if (urlReferenceValue != null && !urlReferenceValue.isEmpty() ){
            return false;
        }

        return true;
    }

    /**
     * Gets the value converted.
     *
     * @return the value converted
     */
    public Object getValueConverted() {
        return valueConverted;
    }

    /**
     * Sets the value converted.
     *
     * @param valueConverted the new value converted
     */
    public void setValueConverted(Object valueConverted) {
        this.valueConverted = valueConverted;
    }

    /**
     * Gets the index type.
     *
     * @return the index type
     */
    public CustomFieldIndexTypeEnum getIndexType() {
        return indexType;
    }

    /**
     * Sets the index type.
     *
     * @param indexType the new index type
     */
    public void setIndexType(CustomFieldIndexTypeEnum indexType) {
        this.indexType = indexType;
    }
   
    /**
     * Gets the file value.
     * 
     * @return the fileValue
     */
    public String getFileValue() {
        return fileValue;
    }

    /**
     * Sets the file value.
     * 
     * @param fileValue the fileValue to set
     */
    public void setFileValue(String fileValue) {
        this.fileValue = fileValue;
    }
    
    /**
     * @return the formattedValue
     */
    public CustomFieldFormattedValueDto getFormattedValue() {
        return formattedValue;
    }

    /**
     * @param formattedValue the formattedValue to set
     */
    public void setFormattedValue(CustomFieldFormattedValueDto formattedValue) {
        this.formattedValue = formattedValue;
    }

    /**
     * Gets the guiPosition
     * @return the guiPosition
     */
    public String getGuiPosition() {
        return guiPosition;
    }

    /**
     * Sets the guiPosition
     *
     * @param guiPosition the guiPosition
     */
    public void setGuiPosition(String guiPosition) {
        this.guiPosition = guiPosition;
    }

    /**
     * Gets custom table code
     *
     * @return the custom table code
     */
    public String getCustomTableCode() {
        return customTableCode;
    }

    /**
     * Sets custom table code
     *
     * @param customTableCode the custom table code
     */
    public void setCustomTableCode(String customTableCode) {
        this.customTableCode = customTableCode;
    }

    /**
     * Gets filters for custom table wrapper
     *
     * @return customTableWrapper's filters
     */
    public String getDataFilter() {
        return dataFilter;
    }

    /**
     * Gets filters for custom table wrapper
     *
     * @param dataFilter
     */
    public void setDataFilter(String dataFilter) {
        this.dataFilter = dataFilter;
    }

    /**
     * Gets CustomTableWrapper's fields
     *
     * @return CustomTableWrapper's fields
     */
    public String getFields() {
        return fields;
    }

    /**
     * Sets CustomTableWrapper's fields
     *
     * @param fields CustomTableWrapper's fields
     */
    public void setFields(String fields) {
        this.fields = fields;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CustomFieldDto{");
        sb.append("code='").append(code).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", fieldType=").append(fieldType);
        sb.append(", valueDate=").append(valueDate);
        sb.append(", valuePeriodStartDate=").append(valuePeriodStartDate);
        sb.append(", valuePeriodEndDate=").append(valuePeriodEndDate);
        sb.append(", valuePeriodPriority=").append(valuePeriodPriority);
        sb.append(", stringValue='").append(stringValue).append('\'');
        sb.append(", dateValue=").append(dateValue);
        sb.append(", longValue=").append(longValue);
        sb.append(", doubleValue=").append(doubleValue);
        sb.append(", booleanValue=").append(booleanValue);
        sb.append(", listValue=").append(listValue);
        sb.append(", mapValue=").append(mapValue);
        sb.append(", entityReferenceValue=").append(entityReferenceValue);
        sb.append(", valueConverted=").append(valueConverted);
        sb.append(", indexType=").append(indexType);
        sb.append(", fileValue='").append(fileValue).append('\'');
        sb.append(", formattedValue=").append(formattedValue);
        sb.append(", urlReferenceValue=").append(urlReferenceValue);
        sb.append('}');
        return sb.toString();
    }

    public UrlReferenceDto getUrlReferenceValue() {
        return urlReferenceValue;
    }

    public void setUrlReferenceValue(UrlReferenceDto urlReferenceValue) {
        this.urlReferenceValue = urlReferenceValue;
    }
}
