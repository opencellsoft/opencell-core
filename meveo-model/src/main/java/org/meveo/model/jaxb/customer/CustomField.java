package org.meveo.model.jaxb.customer;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.CustomFieldTypeEnum;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "customField")
public class CustomField {

    @XmlAttribute(required = true)
    protected String code;

    @XmlAttribute
    protected String description;

    @XmlAttribute
    protected String stringValue;

    @XmlAttribute
    protected Date dateValue;

    @XmlAttribute
    protected Long longValue;

    @XmlAttribute
    protected Double doubleValue;

    public CustomField() {
    }

    public CustomField(CustomFieldInstance cfi) {
        if (cfi != null) {
            code = cfi.getCode();
            description = cfi.getDescription();
            stringValue = cfi.getStringValue();
            dateValue = cfi.getDateValue();
            longValue = cfi.getLongValue();
            doubleValue = cfi.getDoubleValue();
        }
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

    public Object getValueConverted() {
        // if (mapValue != null && !mapValue.isEmpty()) {
        // return mapValue;
        // } else if (listValue != null && !listValue.isEmpty()) {
        // return listValue;
        // } else
        if (stringValue != null) {
            return stringValue;
        } else if (dateValue != null) {
            return dateValue;
        } else if (doubleValue != null) {
            return doubleValue;
        } else if (longValue != null) {
            return longValue;
            // } else if (entityReferenceValue != null) {
            // return entityReferenceValue;
        }
        return null;
    }

    /**
     * Check if value is empty given specific field or storage type
     * 
     * @param fieldType Field type to check
     * @param storageType Storage type to check
     * @return True if value is empty
     * 
     */
    @SuppressWarnings("incomplete-switch")
    public boolean isEmpty(CustomFieldTypeEnum fieldType, CustomFieldStorageTypeEnum storageType) {
        // if (storageType == CustomFieldStorageTypeEnum.MAP) {
        // if (mapValue == null || mapValue.isEmpty()) {
        // return true;
        // }
        //
        // for (Entry<String, CustomFieldValueDto> mapItem : mapValue.entrySet()) {
        // if (mapItem.getKey() == null || mapItem.getKey().isEmpty() || mapItem.getValue() == null || mapItem.getValue().isEmpty()) {
        // return true;
        // }
        // }
        // } else if (storageType == CustomFieldStorageTypeEnum.LIST) {
        // if (listValue == null || listValue.isEmpty()) {
        // return true;
        // }
        //
        // for (CustomFieldValueDto listItem : listValue) {
        // if (listItem == null || listItem.isEmpty()) {
        // return true;
        // }
        // }
        //
        // } else
        if (storageType == CustomFieldStorageTypeEnum.SINGLE) {
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
                // case ENTITY:
                // return entityReferenceValue == null || entityReferenceValue.isEmpty();
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
        // if (mapValue != null) {
        // for (Entry<String, CustomFieldValueDto> mapItem : mapValue.entrySet()) {
        // if (mapItem.getKey() != null && !mapItem.getKey().isEmpty() && mapItem.getValue() != null && mapItem.getValue().isEmpty()) {
        // return false;
        // }
        // }
        // }
        // if (listValue != null) {
        // for (CustomFieldValueDto listItem : listValue) {
        // if (listItem != null && !listItem.isEmpty()) {
        // return false;
        // }
        // }
        // }
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
            // } else if (entityReferenceValue == null && !entityReferenceValue.isEmpty()) {
            // return false;
        }

        return true;
    }
}
