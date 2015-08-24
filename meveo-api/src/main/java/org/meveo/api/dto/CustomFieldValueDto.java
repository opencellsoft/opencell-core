package org.meveo.api.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.crm.CustomFieldValue;
import org.meveo.model.crm.EntityReferenceWrapper;

/**
 * @author Andrius Karpavicius
 **/
@XmlRootElement(name = "CustomFieldValue")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomFieldValueDto implements Serializable {

    private static final long serialVersionUID = -6551785257592739335L;

    @XmlElements({ @XmlElement(name = "dateValue", type = Date.class), @XmlElement(name = "doubleValue", type = Double.class), @XmlElement(name = "longValue", type = Long.class),
            @XmlElement(name = "stringValue", type = String.class), @XmlElement(name = "entityReferenceValue", type = EntityReferenceDto.class) })
    protected Object value;

    public CustomFieldValueDto() {
    }

    public static CustomFieldValueDto toDTO(CustomFieldValue cfv) {
        CustomFieldValueDto dto = new CustomFieldValueDto();
        Object singleValue = cfv.getSingleValue();
        if (singleValue != null && singleValue instanceof EntityReferenceWrapper) {
            singleValue = new EntityReferenceDto((EntityReferenceWrapper) singleValue);
        }
        dto.value = singleValue;
        return dto;
    }

    public Object fromDTO() {

        if (value instanceof EntityReferenceDto) {
            return ((EntityReferenceDto) value).fromDTO();
        } else {
            return value;
        }
    }

    public static List<CustomFieldValueDto> toDTO(List<Object> listValue) {

        List<CustomFieldValueDto> dtos = new ArrayList<CustomFieldValueDto>();

        for (Object listItem : listValue) {
            CustomFieldValueDto dto = new CustomFieldValueDto();
            if (listItem instanceof EntityReferenceWrapper) {
                dto.value = new EntityReferenceDto((EntityReferenceWrapper) listItem);
            } else {
                dto.value = listItem;
            }
            dtos.add(dto);
        }
        return dtos;
    }

    public static List<Object> fromDTO(List<CustomFieldValueDto> listValue) {
        List<Object> values = new ArrayList<Object>();
        for (CustomFieldValueDto valueDto : listValue) {
            values.add(valueDto.fromDTO());
        }
        return values;
    }

    public static Map<String, Object> fromDTO(Map<String, CustomFieldValueDto> mapValue) {
        Map<String, Object> values = new HashMap<String, Object>();
        for (Map.Entry<String, CustomFieldValueDto> valueDto : mapValue.entrySet()) {
            values.put(valueDto.getKey(), valueDto.getValue().fromDTO());
        }
        return values;
    }

    public static Map<String, CustomFieldValueDto> toDTO(Map<String, Object> mapValue) {

        Map<String, CustomFieldValueDto> dtos = new HashMap<String, CustomFieldValueDto>();

        for (Map.Entry<String, Object> mapItem : mapValue.entrySet()) {
            CustomFieldValueDto dto = new CustomFieldValueDto();
            if (mapItem.getValue() instanceof EntityReferenceWrapper) {
                dto.value = new EntityReferenceDto((EntityReferenceWrapper) mapItem.getValue());
            } else {
                dto.value = mapItem.getValue();
            }
            dtos.put(mapItem.getKey(), dto);
        }
        return dtos;
    }

    public CustomFieldValueDto(Object e) {
        this.value = e;
    }

    @Override
    public String toString() {
        return String.format("CustomFieldValueDto [value=%s]", value);
    }

    /**
     * Check if value is empty
     * 
     * @return True if value is empty
     */
    public boolean isEmpty() {
        if (value == null) {
            return true;
        }
        if (value instanceof EntityReferenceDto) {
            return ((EntityReferenceDto) value).isEmpty();
        } else if (value instanceof String) {
            return ((String) value).length() == 0;
        }
        return false;
    }
}