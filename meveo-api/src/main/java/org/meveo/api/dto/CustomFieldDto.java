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
            dto.setStringValue(cfi.getValue().getStringValue());
            dto.setDateValue(cfi.getValue().getDateValue());
            dto.setLongValue(cfi.getValue().getLongValue());
            dto.setDoubleValue(cfi.getValue().getDoubleValue());
            dto.setListValue(CustomFieldValueDto.toDTO(cfi.getValue().getListValue()));
            dto.setMapValue(CustomFieldValueDto.toDTO(cfi.getValue().getMapValue()));
            if (cfi.getValue().getEntityReferenceValue() != null) {
                dto.setEntityReferenceValue(new EntityReferenceDto(cfi.getValue().getEntityReferenceValue()));
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
                dto.setStringValue(period.getValue().getStringValue());
                dto.setDateValue(period.getValue().getDateValue());
                dto.setLongValue(period.getValue().getLongValue());
                dto.setDoubleValue(period.getValue().getDoubleValue());
                dto.setListValue(CustomFieldValueDto.toDTO(period.getValue().getListValue()));
                dto.setMapValue(CustomFieldValueDto.toDTO(period.getValue().getMapValue()));
                if (period.getValue().getEntityReferenceValue() != null) {
                    dto.setEntityReferenceValue(new EntityReferenceDto(period.getValue().getEntityReferenceValue()));
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
     * Check if value is empty
     * 
     * @param fieldType Field type to check
     * @return
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

    @Override
    public String toString() {
        return String
            .format(
                "CustomFieldDto [code=%s, description=%s, valueDate=%s, valuePeriodStartDate=%s, valuePeriodEndDate=%s, valuePeriodPriority=%s, stringValue=%s, dateValue=%s, longValue=%s, doubleValue=%s]",
                code, description, valueDate, valuePeriodStartDate, valuePeriodEndDate, valuePeriodPriority, stringValue, dateValue, longValue, doubleValue);
    }
}