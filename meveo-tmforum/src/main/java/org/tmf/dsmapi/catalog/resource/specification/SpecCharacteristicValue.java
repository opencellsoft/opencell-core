package org.tmf.dsmapi.catalog.resource.specification;

import java.io.Serializable;
import org.codehaus.jackson.annotate.JsonProperty;
import org.tmf.dsmapi.catalog.resource.TimeRange;
import org.tmf.dsmapi.commons.Utilities;

/**
 *
 * @author pierregauthier
 *
 * {
 *     "valueType": "number",
 *     "default": "true",
 *     "value": "4.2",
 *     "unitOfMeasure": "inches",
 *     "valueFrom": "",
 *     "valueTo": "",
 *     "validFor": {
 *         "startDateTime": "2013-04-19T16:42:23-04:00",
 *         "endDateTime": ""
 *     }
 * }
 *
 */
public class SpecCharacteristicValue implements Serializable {
    private static final long serialVersionUID = 1L;

    private CharacteristicValueType valueType;

    @JsonProperty("default")
    private Boolean defaultValue;

    private String value;
    private String unitOfMeasure;
    private String valueFrom;
    private String valueTo;
    private TimeRange validFor;

    public CharacteristicValueType getValueType() {
        return valueType;
    }

    public void setValueType(CharacteristicValueType valueType) {
        this.valueType = valueType;
    }

    public Boolean getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Boolean defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public String getValueFrom() {
        return valueFrom;
    }

    public void setValueFrom(String valueFrom) {
        this.valueFrom = valueFrom;
    }

    public String getValueTo() {
        return valueTo;
    }

    public void setValueTo(String valueTo) {
        this.valueTo = valueTo;
    }

    public TimeRange getValidFor() {
        return validFor;
    }

    public void setValidFor(TimeRange validFor) {
        this.validFor = validFor;
    }

    @JsonProperty(value = "validFor")
    public TimeRange validForToJson() {
        return (validFor != null && validFor.isEmpty() == false) ? validFor : null;
    }

    @Override
    public int hashCode() {
        int hash = 3;

        hash = 83 * hash + (this.valueType != null ? this.valueType.hashCode() : 0);
        hash = 83 * hash + (this.defaultValue != null ? this.defaultValue.hashCode() : 0);
        hash = 83 * hash + (this.value != null ? this.value.hashCode() : 0);
        hash = 83 * hash + (this.unitOfMeasure != null ? this.unitOfMeasure.hashCode() : 0);
        hash = 83 * hash + (this.valueFrom != null ? this.valueFrom.hashCode() : 0);
        hash = 83 * hash + (this.valueTo != null ? this.valueTo.hashCode() : 0);
        hash = 83 * hash + (this.validFor != null ? this.validFor.hashCode() : 0);

        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        final SpecCharacteristicValue other = (SpecCharacteristicValue) object;
        if (Utilities.areEqual(this.valueType, other.valueType) == false) {
            return false;
        }

        if (Utilities.areEqual(this.defaultValue, other.defaultValue) == false) {
            return false;
        }

        if (Utilities.areEqual(this.value, other.value) == false) {
            return false;
        }

        if (Utilities.areEqual(this.unitOfMeasure, other.unitOfMeasure) == false) {
            return false;
        }

        if (Utilities.areEqual(this.valueFrom, other.valueFrom) == false) {
            return false;
        }

        if (Utilities.areEqual(this.valueTo, other.valueTo) == false) {
            return false;
        }

        if (Utilities.areEqual(this.validFor, other.validFor) == false) {
            return false;
        }
        
        return true;
    }

    @Override
    public String toString() {
        return "SpecCharacteristicValue{" + "valueType=" + valueType + ", defaultValue=" + defaultValue + ", value=" + value + ", unitOfMeasure=" + unitOfMeasure + ", valueFrom=" + valueFrom + ", valueTo=" + valueTo + ", validFor=" + validFor + '}';
    }

    public static SpecCharacteristicValue createProto() {
        SpecCharacteristicValue specCharacteristicValue = new SpecCharacteristicValue();

        specCharacteristicValue.valueType = CharacteristicValueType.STRING;
        specCharacteristicValue.defaultValue = false;
        specCharacteristicValue.value = "value";
        specCharacteristicValue.unitOfMeasure = "kilo";
        specCharacteristicValue.valueFrom = "value from";
        specCharacteristicValue.valueTo = "value to";
        specCharacteristicValue.validFor = TimeRange.createProto();

        return specCharacteristicValue;
    }

}
