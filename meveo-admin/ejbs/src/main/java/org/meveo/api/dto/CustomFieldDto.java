package org.meveo.api.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.CustomFieldPeriod;
import org.meveo.model.crm.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.CustomFieldTypeEnum;

@XmlRootElement(name = "CustomField")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomFieldDto {

    @XmlAttribute(required = true)
    protected String code;

    @XmlAttribute
    protected String description;

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

    public CustomFieldDto() {
    }

    public static List<CustomFieldDto> toDTO(CustomFieldInstance cfi) {
        List<CustomFieldDto> dtos = new ArrayList<CustomFieldDto>();
        if (!cfi.isVersionable()) {
            CustomFieldDto dto = new CustomFieldDto();
            dto.setCode(cfi.getCode());
            dto.setDescription(cfi.getDescription());
            dto.setStringValue(cfi.getCfValue().getStringValue());
            dto.setDateValue(cfi.getCfValue().getDateValue());
            dto.setLongValue(cfi.getCfValue().getLongValue());
            dto.setDoubleValue(cfi.getCfValue().getDoubleValue());
            dto.setListValue(CustomFieldValueDto.toDTO(cfi.getCfValue().getListValue()));
            dto.setMapValue(CustomFieldValueDto.toDTO(cfi.getCfValue().getMapValue()));
            if (cfi.getCfValue().getEntityReferenceValue() != null) {
                dto.setEntityReferenceValue(new EntityReferenceDto(cfi.getCfValue().getEntityReferenceValue()));
            }
            dtos.add(dto);

        } else {

            for (CustomFieldPeriod period : cfi.getValuePeriods()) {
                CustomFieldDto dto = new CustomFieldDto();
                dto.setCode(cfi.getCode());
                dto.setDescription(cfi.getDescription());
                dto.setValuePeriodStartDate(period.getPeriodStartDate());
                dto.setValuePeriodEndDate(period.getPeriodEndDate());
                if (period.getPriority() > 0) {
                    dto.setValuePeriodPriority(period.getPriority());
                }
                dto.setStringValue(period.getCfValue().getStringValue());
                dto.setDateValue(period.getCfValue().getDateValue());
                dto.setLongValue(period.getCfValue().getLongValue());
                dto.setDoubleValue(period.getCfValue().getDoubleValue());
                dto.setListValue(CustomFieldValueDto.toDTO(period.getCfValue().getListValue()));
                dto.setMapValue(CustomFieldValueDto.toDTO(period.getCfValue().getMapValue()));
                if (period.getCfValue().getEntityReferenceValue() != null) {
                    dto.setEntityReferenceValue(new EntityReferenceDto(period.getCfValue().getEntityReferenceValue()));
                }

                dtos.add(dto);
            }
        }

        return dtos;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
     * Get a value converted from DTO a propper Map, List, EntityWrapper, Date, Long, Double or String value
     * 
     * @return
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
        return String
            .format(
                "CustomFieldDto [code=%s, description=%s, valueDate=%s, valuePeriodStartDate=%s, valuePeriodEndDate=%s, valuePeriodPriority=%s, stringValue=%s, dateValue=%s, longValue=%s, doubleValue=%s]",
                code, description, valueDate, valuePeriodStartDate, valuePeriodEndDate, valuePeriodPriority, stringValue, dateValue, longValue, doubleValue);
    }
}