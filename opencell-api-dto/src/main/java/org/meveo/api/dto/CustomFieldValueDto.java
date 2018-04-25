package org.meveo.api.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a single CF simple value inside a more complex CF value (list, map, matrix).
 *
 * @author Andrius Karpavicius
 */
@XmlRootElement(name = "CustomFieldValue")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomFieldValueDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6551785257592739335L;

    /** The value. */
    @XmlElements({ @XmlElement(name = "dateValue", type = Date.class), @XmlElement(name = "doubleValue", type = Double.class), @XmlElement(name = "longValue", type = Long.class),
            @XmlElement(name = "stringValue", type = String.class), @XmlElement(name = "entityReferenceValue", type = EntityReferenceDto.class),
            @XmlElement(name = "childEntityValue", type = CustomEntityInstanceDto.class) })
    protected Object value;

    /**
     * Instantiates a new custom field value dto.
     */
    public CustomFieldValueDto() {
    }

    /**
     * From DTO.
     *
     * @return the object
     */
    private Object fromDTO() {

        if (value instanceof EntityReferenceDto) {
            return ((EntityReferenceDto) value).fromDTO();
        } else {
            return value;
        }
    }

    /**
     * From DTO.
     *
     * @param listValue the list value
     * @return the list
     */
    public static List<Object> fromDTO(List<CustomFieldValueDto> listValue) {
        List<Object> values = new ArrayList<Object>();
        for (CustomFieldValueDto valueDto : listValue) {
            values.add(valueDto.fromDTO());
        }
        return values;
    }

    /**
     * From DTO.
     *
     * @param mapValue the map value
     * @return the linked hash map
     */
    public static LinkedHashMap<String, Object> fromDTO(Map<String, CustomFieldValueDto> mapValue) {
        LinkedHashMap<String, Object> values = new LinkedHashMap<String, Object>();
        for (Map.Entry<String, CustomFieldValueDto> valueDto : mapValue.entrySet()) {
            values.put(valueDto.getKey(), valueDto.getValue().fromDTO());
        }
        return values;
    }

    /**
     * Instantiates a new custom field value dto.
     *
     * @param object the CustomFieldValue
     */
    public CustomFieldValueDto(Object object) {
        this.value = object;
    }

    /**
     * Check if value is empty.
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

    /**
     * Sets the value.
     *
     * @param value the new value
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    public Object getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return String.format("CustomFieldValueDto [value=%s]", value);
    }
}