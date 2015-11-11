package org.tmf.dsmapi.catalog.resource.specification;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.tmf.dsmapi.catalog.resource.TimeRange;
import org.tmf.dsmapi.commons.Utilities;

/**
 *
 * @author pierregauthier
 *
 * {
 *     "valueType": "number",
 *     "default": true,
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
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Embeddable
public class SpecificationCharacteristicValue implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "VALUE_TYPE", nullable = true)
    private CharacteristicValueType valueType;

    @Column(name = "DEFAULT_VALUE", nullable = true)
    @JsonProperty("default")
    private Boolean defaultValue;

    @Column(name = "THE_VALUE", nullable = true)
    private String value;

    @Column(name = "UNIT_OF_MEASURE", nullable = true)
    private String unitOfMeasure;

    @Column(name = "VALUE_FROM", nullable = true)
    private String valueFrom;

    @Column(name = "VALUE_TO", nullable = true)
    private String valueTo;

    private TimeRange validFor;

    public SpecificationCharacteristicValue() {
    }

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

        hash = 41 * hash + (this.valueType != null ? this.valueType.hashCode() : 0);
        hash = 41 * hash + (this.defaultValue != null ? this.defaultValue.hashCode() : 0);
        hash = 41 * hash + (this.value != null ? this.value.hashCode() : 0);
        hash = 41 * hash + (this.unitOfMeasure != null ? this.unitOfMeasure.hashCode() : 0);
        hash = 41 * hash + (this.valueFrom != null ? this.valueFrom.hashCode() : 0);
        hash = 41 * hash + (this.valueTo != null ? this.valueTo.hashCode() : 0);
        hash = 41 * hash + (this.validFor != null ? this.validFor.hashCode() : 0);

        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        final SpecificationCharacteristicValue other = (SpecificationCharacteristicValue) object;
        if (this.valueType != other.valueType) {
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
        return "SpecificationCharacteristicValue{" + "valueType=" + valueType + ", defaultValue=" + defaultValue + ", value=" + value + ", unitOfMeasure=" + unitOfMeasure + ", valueFrom=" + valueFrom + ", valueTo=" + valueTo + ", validFor=" + validFor + '}';
    }

    public static SpecificationCharacteristicValue createProto() {
        SpecificationCharacteristicValue specificationCharacteristicValue = new SpecificationCharacteristicValue();

        specificationCharacteristicValue.valueType = CharacteristicValueType.STRING;
        specificationCharacteristicValue.defaultValue = false;
        specificationCharacteristicValue.value = "value";
        specificationCharacteristicValue.unitOfMeasure = "kilo";
        specificationCharacteristicValue.valueFrom = "value from";
        specificationCharacteristicValue.valueTo = "value to";
        specificationCharacteristicValue.validFor = TimeRange.createProto();

        return specificationCharacteristicValue;
    }

}
