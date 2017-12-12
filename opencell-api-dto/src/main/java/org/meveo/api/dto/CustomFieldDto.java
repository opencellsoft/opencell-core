package org.meveo.api.dto;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.meveo.model.crm.custom.CustomFieldIndexTypeEnum;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@XmlRootElement(name = "CustomField")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonInclude(Include.NON_NULL)
public class CustomFieldDto {

    @XmlAttribute(required = true)
    protected String code;

    @XmlAttribute
    protected Date valueDate;

    @XmlAttribute
    protected Date valuePeriodStartDate;

    @XmlAttribute
    protected Date valuePeriodEndDate;

    @XmlAttribute
    protected Integer valuePeriodPriority;

    @XmlElement
    protected String stringValue;

    @XmlElement
    protected Date dateValue;

    @XmlElement
    protected Long longValue;

    @XmlElement()
    protected Double doubleValue;

    @XmlElementWrapper(name = "listValue")
    @XmlElement(name = "value")
    protected List<CustomFieldValueDto> listValue;

    // DO NOT change to Map. Used LinkedHashMap to preserve the item order during read/write
    @XmlElement
    protected LinkedHashMap<String, CustomFieldValueDto> mapValue;

    @XmlElement()
    protected EntityReferenceDto entityReferenceValue;

    // A transient object. Contains a converted value from DTO to some object when it is applicable
    @XmlTransient
    protected Object valueConverted;
    
    @XmlElement()
    private CustomFieldIndexTypeEnum indexType;

    public CustomFieldDto() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public Date getDateValue() {
        return dateValue;
    }

    public void setDateValue(Date dateValue) {
        this.dateValue = dateValue;
    }

    public Long getLongValue() {
        return longValue;
    }

    public void setLongValue(Long longValue) {
        this.longValue = longValue;
    }

    public Double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(Double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public Date getValueDate() {
        return valueDate;
    }

    public void setValueDate(Date valueDate) {
        this.valueDate = valueDate;
    }

    public Date getValuePeriodStartDate() {
        return valuePeriodStartDate;
    }

    public void setValuePeriodStartDate(Date valuePeriodStartDate) {
        this.valuePeriodStartDate = valuePeriodStartDate;
    }

    public Date getValuePeriodEndDate() {
        return valuePeriodEndDate;
    }

    public void setValuePeriodEndDate(Date valuePeriodEndDate) {
        this.valuePeriodEndDate = valuePeriodEndDate;
    }

    public void setValuePeriodPriority(Integer valuePeriodPriority) {
        this.valuePeriodPriority = valuePeriodPriority;
    }

    public Integer getValuePeriodPriority() {
        return valuePeriodPriority;
    }

    public List<CustomFieldValueDto> getListValue() {
        return listValue;
    }

    public void setListValue(List<CustomFieldValueDto> listValue) {
        this.listValue = listValue;
    }

    public Map<String, CustomFieldValueDto> getMapValue() {
        return mapValue;
    }

    public void setMapValue(LinkedHashMap<String, CustomFieldValueDto> mapValue) {
        this.mapValue = mapValue;
    }

    public EntityReferenceDto getEntityReferenceValue() {
        return entityReferenceValue;
    }

    public void setEntityReferenceValue(EntityReferenceDto entityReferenceValue) {
        this.entityReferenceValue = entityReferenceValue;
    }

    /**
     * Check if value is empty given specific field or storage type
     * 
     * @param fieldType Field type to check
     * @param storageType Storage type to check
     * @return True if value is empty
     * 
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
            }
        }
        return false;
    }

    /**
     * A generic way to check if value is empty
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
        } else if (doubleValue != null) {
            return false;
        } else if (longValue != null) {
            return false;
        } else if (stringValue != null && !stringValue.isEmpty()) {
            return false;
        } else if (entityReferenceValue == null && !entityReferenceValue.isEmpty()) {
            return false;
        }

        return true;
    }

    public Object getValueConverted() {
        return valueConverted;
    }

    public void setValueConverted(Object valueConverted) {
        this.valueConverted = valueConverted;
    }
    
    public CustomFieldIndexTypeEnum getIndexType() {
        return indexType;
    }
    
    public void setIndexType(CustomFieldIndexTypeEnum indexType) {
        this.indexType = indexType;
    }


}