package org.meveo.api.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.CustomFieldPeriod;

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

    @XmlElement
    protected Double doubleValue;

    public CustomFieldDto() {
    }

    public static List<CustomFieldDto> toDTO(CustomFieldInstance cfi) {
        List<CustomFieldDto> dtos = new ArrayList<CustomFieldDto>();
        if (!cfi.isVersionable()) {
            CustomFieldDto dto = new CustomFieldDto();
            dto.setCode(cfi.getCode());
            dto.setDescription(cfi.getDescription());
            dto.setStringValue(cfi.getStringValue());
            dto.setDateValue(cfi.getDateValue());
            dto.setLongValue(cfi.getLongValue());
            dto.setDoubleValue(cfi.getDoubleValue());
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
                dto.setStringValue(period.getStringValue());
                dto.setDateValue(period.getDateValue());
                dto.setLongValue(period.getLongValue());
                dto.setDoubleValue(period.getDoubleValue());
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

    @Override
    public String toString() {
        return String
            .format(
                "CustomFieldDto [code=%s, description=%s, valueDate=%s, valuePeriodStartDate=%s, valuePeriodEndDate=%s, valuePeriodPriority=%s, stringValue=%s, dateValue=%s, longValue=%s, doubleValue=%s]",
                code, description, valueDate, valuePeriodStartDate, valuePeriodEndDate, valuePeriodPriority, stringValue, dateValue, longValue, doubleValue);
    }
}