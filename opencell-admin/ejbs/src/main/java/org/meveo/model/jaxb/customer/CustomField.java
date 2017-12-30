package org.meveo.model.jaxb.customer;

import java.util.ArrayList;
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

import org.meveo.api.dto.CustomFieldValueDto;
import org.meveo.api.dto.EntityReferenceDto;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.crm.custom.CustomFieldValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "customField")
public class CustomField {

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

    @XmlElement()
    protected Map<String, CustomFieldValueDto> mapValue;

    @XmlElement()
    protected EntityReferenceDto entityReferenceValue;

    public CustomField() {
    }

    @SuppressWarnings("unchecked")
    public static CustomField toDTO(String cfCode, CustomFieldValue cfValue) {

        CustomField dto = new CustomField();
        dto.setCode(cfCode);
        if (cfValue.getPeriod() != null) {
            dto.setValuePeriodStartDate(cfValue.getPeriod().getFrom());
            dto.setValuePeriodEndDate(cfValue.getPeriod().getTo());
        }

        if (cfValue.getPriority() > 0) {
            dto.setValuePeriodPriority(cfValue.getPriority());
        }
        dto.setStringValue(cfValue.getStringValue());
        dto.setDateValue(cfValue.getDateValue());
        dto.setLongValue(cfValue.getLongValue());
        dto.setDoubleValue(cfValue.getDoubleValue());
        dto.setListValue(customFieldValueToDTO(cfValue.getListValue()));
        dto.setMapValue(customFieldValueToDTO(cfValue.getMapValue()));
        if (cfValue.getEntityReferenceValue() != null) {
            dto.setEntityReferenceValue(new EntityReferenceDto(cfValue.getEntityReferenceValue()));
        }

        return dto;
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

    public void setMapValue(Map<String, CustomFieldValueDto> mapValue) {
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
        if (storageType == CustomFieldStorageTypeEnum.MAP) {
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
            case CHILD_ENTITY:
                return entityReferenceValue == null || entityReferenceValue.isEmpty();
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

    /**
     * Get a value converted from DTO a propper Map, List, EntityWrapper, Date, Long, Double or String value.
     * 
     * @return converted value.
     */
    public Object getValueConverted() {
        if (mapValue != null && !mapValue.isEmpty()) {
            return CustomFieldValueDto.fromDTO(mapValue);
        } else if (listValue != null && !listValue.isEmpty()) {
            return CustomFieldValueDto.fromDTO(listValue);
        } else if (stringValue != null) {
            return stringValue;
        } else if (dateValue != null) {
            return dateValue;
        } else if (doubleValue != null) {
            return doubleValue;
        } else if (longValue != null) {
            return longValue;
        } else if (entityReferenceValue != null) {
            return entityReferenceValue.fromDTO();
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format(
            "CustomField [code=%s, valueDate=%s, valuePeriodStartDate=%s, valuePeriodEndDate=%s, valuePeriodPriority=%s, stringValue=%s, dateValue=%s, longValue=%s, doubleValue=%s,mapValue="
                    + mapValue + "]",
            code, valueDate, valuePeriodStartDate, valuePeriodEndDate, valuePeriodPriority, stringValue, dateValue, longValue, doubleValue);
    }

    private static List<CustomFieldValueDto> customFieldValueToDTO(List<Object> listValue) {

        if (listValue == null) {
            return null;
        }
        List<CustomFieldValueDto> dtos = new ArrayList<CustomFieldValueDto>();

        for (Object listItem : listValue) {
            CustomFieldValueDto dto = new CustomFieldValueDto();
            if (listItem instanceof EntityReferenceWrapper) {
                dto.setValue(new EntityReferenceDto((EntityReferenceWrapper) listItem));
            } else {
                dto.setValue(listItem);
            }
            dtos.add(dto);
        }
        return dtos;
    }

    private static LinkedHashMap<String, CustomFieldValueDto> customFieldValueToDTO(Map<String, Object> mapValue) {
        if (mapValue == null || mapValue.entrySet().size() == 0) {
            return null;
        }
        LinkedHashMap<String, CustomFieldValueDto> dtos = new LinkedHashMap<String, CustomFieldValueDto>();

        for (Map.Entry<String, Object> mapItem : mapValue.entrySet()) {
            CustomFieldValueDto dto = new CustomFieldValueDto();
            if (mapItem.getValue() instanceof EntityReferenceWrapper) {
                dto.setValue(new EntityReferenceDto((EntityReferenceWrapper) mapItem.getValue()));
            } else {
                dto.setValue(mapItem.getValue());
            }
            dtos.put(mapItem.getKey(), dto);
        }
        return dtos;
    }
}
