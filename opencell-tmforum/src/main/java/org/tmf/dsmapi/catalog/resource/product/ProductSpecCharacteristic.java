package org.tmf.dsmapi.catalog.resource.product;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.tmf.dsmapi.catalog.resource.TimeRange;
import org.tmf.dsmapi.catalog.resource.specification.CharacteristicValueType;
import org.tmf.dsmapi.catalog.resource.specification.SpecificationCharacteristicRelationship;
import org.tmf.dsmapi.catalog.resource.specification.SpecificationCharacteristicValue;
import org.tmf.dsmapi.commons.Utilities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author pierregauthier
 * 
 *         { "id": "42", "name": "Screen Size", "description": "Screen size", "valueType": "number", "configurable": "false", "validFor": { "startDateTime":
 *         "2013-04-19T16:42:23-04:00", "endDateTime": "" }, "productSpecCharRelationship": [ { "type": "dependency", "id": "43", "validFor": { "startDateTime":
 *         "2013-04-19T16:42:23-04:00", "endDateTime": "" } } ], "productSpecCharacteristicValue": [ { "valueType": "number", "default": "true", "value": "4.2", "unitOfMeasure":
 *         "inches", "valueFrom": "", "valueTo": "", "validFor": { "startDateTime": "2013-04-19T16:42:23-04:00", "endDateTime": "" } } ] }
 * 
 */
@XmlRootElement(name="ProductSpecCharacteristic", namespace="http://www.tmforum.org")
@XmlType(name="ProductSpecCharacteristic", namespace="http://www.tmforum.org")
@JsonInclude(value = Include.NON_NULL)
public class ProductSpecCharacteristic implements Serializable {
    public final static long serialVersionUID = 1L;

    private String id;
    private String name;
    private String description;
    private CharacteristicValueType valueType;
    private Boolean configurable;
    private TimeRange validFor;
    private List<SpecificationCharacteristicRelationship> productSpecCharRelationship;
    private List<SpecificationCharacteristicValue> productSpecCharacteristicValue;

    public ProductSpecCharacteristic() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CharacteristicValueType getValueType() {
        return valueType;
    }

    public void setValueType(CharacteristicValueType valueType) {
        this.valueType = valueType;
    }

    public Boolean getConfigurable() {
        return configurable;
    }

    public void setConfigurable(Boolean configurable) {
        this.configurable = configurable;
    }

    public TimeRange getValidFor() {
        return validFor;
    }

    public void setValidFor(TimeRange validFor) {
        this.validFor = validFor;
    }

    public List<SpecificationCharacteristicRelationship> getProductSpecCharRelationship() {
        return productSpecCharRelationship;
    }

    public void setProductSpecCharRelationship(List<SpecificationCharacteristicRelationship> productSpecCharRelationship) {
        this.productSpecCharRelationship = productSpecCharRelationship;
    }

    public List<SpecificationCharacteristicValue> getProductSpecCharacteristicValue() {
        return productSpecCharacteristicValue;
    }

    public void setProductSpecCharacteristicValue(List<SpecificationCharacteristicValue> productSpecCharacteristicValue) {
        this.productSpecCharacteristicValue = productSpecCharacteristicValue;
    }

    @JsonProperty(value = "validFor")
    public TimeRange validForToJson() {
        return (validFor != null && validFor.isEmpty() == false) ? validFor : null;
    }

    @JsonProperty(value = "productSpecCharRelationship")
    public List<SpecificationCharacteristicRelationship> productSpecCharRelationshipToJson() {
        return (productSpecCharRelationship != null && productSpecCharRelationship.size() > 0) ? productSpecCharRelationship : null;
    }

    @JsonProperty(value = "productSpecCharacteristicValue")
    public List<SpecificationCharacteristicValue> productSpecCharacteristicValueToJson() {
        return (productSpecCharacteristicValue != null && productSpecCharacteristicValue.size() > 0) ? productSpecCharacteristicValue : null;
    }

    @Override
    public int hashCode() {
        int hash = 5;

        hash = 71 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 71 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 71 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 71 * hash + (this.valueType != null ? this.valueType.hashCode() : 0);
        hash = 71 * hash + (this.configurable != null ? this.configurable.hashCode() : 0);
        hash = 71 * hash + (this.validFor != null ? this.validFor.hashCode() : 0);
        hash = 71 * hash + (this.productSpecCharRelationship != null ? this.productSpecCharRelationship.hashCode() : 0);
        hash = 71 * hash + (this.productSpecCharacteristicValue != null ? this.productSpecCharacteristicValue.hashCode() : 0);

        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        final ProductSpecCharacteristic other = (ProductSpecCharacteristic) object;
        if (Utilities.areEqual(this.id, other.id) == false) {
            return false;
        }

        if (Utilities.areEqual(this.name, other.name) == false) {
            return false;
        }

        if (Utilities.areEqual(this.description, other.description) == false) {
            return false;
        }

        if (this.valueType != other.valueType) {
            return false;
        }

        if (Utilities.areEqual(this.configurable, other.configurable) == false) {
            return false;
        }

        if (Utilities.areEqual(this.validFor, other.validFor) == false) {
            return false;
        }

        if (Utilities.areEqual(this.productSpecCharRelationship, other.productSpecCharRelationship) == false) {
            return false;
        }

        if (Utilities.areEqual(this.productSpecCharacteristicValue, other.productSpecCharacteristicValue) == false) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "ProductSpecCharacteristic{" + "id=" + id + ", name=" + name + ", description=" + description + ", valueType=" + valueType + ", configurable=" + configurable
                + ", validFor=" + validFor + ", productSpecCharRelationship=" + productSpecCharRelationship + ", productSpecCharacteristicValue=" + productSpecCharacteristicValue
                + '}';
    }

    @JsonIgnore
    public boolean isValid() {
        if (validateCharacteristicValues() == false) {
            return false;
        }

        return true;
    }

    private boolean validateCharacteristicValues() {
        if (Utilities.hasContents(this.productSpecCharacteristicValue) == false) {
            return true;
        }

        int defaultCount = 0;
        for (SpecificationCharacteristicValue characteristicValue : this.productSpecCharacteristicValue) {
            if (characteristicValue.getDefaultValue() == Boolean.TRUE) {
                defaultCount++;
            }
        }

        if (defaultCount > 1) {
            return false;
        }

        return true;
    }
}