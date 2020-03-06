/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.tmf.dsmapi.catalog.resource.service;

import java.io.Serializable;
import java.util.List;

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
 * @author bahman.barzideh
 * 
 *         { "id": "34", "name": "Screen Size", "description": "Screen size", "valueType": "number", "configurable": false, "validFor": { "startDateTime":
 *         "2013-04-19T16:42:23-04:00", "endDateTime": "" }, "serviceSpecCharRelationship": [ { "type": "dependency", "id": "43", "validFor": { "startDateTime":
 *         "2013-04-19T16:42:23-04:00", "endDateTime": "" } } ], "serviceSpecCharacteristicValue": [ { "valueType": "number", "default": true, "value": "4.2", "unitOfMeasure":
 *         "inches", "valueFrom": "", "valueTo": "", "validFor": { "startDateTime": "2013-04-19T16:42:23-04:00", "endDateTime": "" } } ] }
 * 
 */
@JsonInclude(value = Include.NON_NULL)
public class ServiceSpecCharacteristic implements Serializable {
    public final static long serialVersionUID = 1L;

    private String id;
    private String name;
    private String description;
    private TimeRange validFor;
    private CharacteristicValueType valueType;
    private Boolean configurable;
    private List<SpecificationCharacteristicRelationship> serviceSpecCharRelationship;
    private List<SpecificationCharacteristicValue> serviceSpecCharacteristicValue;

    public ServiceSpecCharacteristic() {
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

    public TimeRange getValidFor() {
        return validFor;
    }

    public void setValidFor(TimeRange validFor) {
        this.validFor = validFor;
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

    public List<SpecificationCharacteristicRelationship> getServiceSpecCharRelationship() {
        return serviceSpecCharRelationship;
    }

    public void setServiceSpecCharRelationship(List<SpecificationCharacteristicRelationship> serviceSpecCharRelationship) {
        this.serviceSpecCharRelationship = serviceSpecCharRelationship;
    }

    public List<SpecificationCharacteristicValue> getServiceSpecCharacteristicValue() {
        return serviceSpecCharacteristicValue;
    }

    public void setServiceSpecCharacteristicValue(List<SpecificationCharacteristicValue> serviceSpecCharacteristicValue) {
        this.serviceSpecCharacteristicValue = serviceSpecCharacteristicValue;
    }

    @JsonProperty(value = "validFor")
    public TimeRange validForToJson() {
        return (validFor != null && validFor.isEmpty() == false) ? validFor : null;
    }

    @JsonProperty(value = "serviceSpecCharRelationship")
    public List<SpecificationCharacteristicRelationship> serviceSpecCharRelationshipToJson() {
        return (serviceSpecCharRelationship != null && serviceSpecCharRelationship.size() > 0) ? serviceSpecCharRelationship : null;
    }

    @JsonProperty(value = "serviceSpecCharacteristicValue")
    public List<SpecificationCharacteristicValue> serviceSpecCharacteristicValueToJson() {
        return (serviceSpecCharacteristicValue != null && serviceSpecCharacteristicValue.size() > 0) ? serviceSpecCharacteristicValue : null;
    }

    @Override
    public int hashCode() {
        int hash = 5;

        hash = 67 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 67 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 67 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 67 * hash + (this.validFor != null ? this.validFor.hashCode() : 0);
        hash = 67 * hash + (this.valueType != null ? this.valueType.hashCode() : 0);
        hash = 67 * hash + (this.configurable != null ? this.configurable.hashCode() : 0);
        hash = 67 * hash + (this.serviceSpecCharRelationship != null ? this.serviceSpecCharRelationship.hashCode() : 0);
        hash = 67 * hash + (this.serviceSpecCharacteristicValue != null ? this.serviceSpecCharacteristicValue.hashCode() : 0);

        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        final ServiceSpecCharacteristic other = (ServiceSpecCharacteristic) object;
        if (Utilities.areEqual(this.id, other.id) == false) {
            return false;
        }

        if (Utilities.areEqual(this.name, other.name) == false) {
            return false;
        }

        if (Utilities.areEqual(this.description, other.description) == false) {
            return false;
        }

        if (Utilities.areEqual(this.validFor, other.validFor) == false) {
            return false;
        }

        if (this.valueType != other.valueType) {
            return false;
        }

        if (Utilities.areEqual(this.configurable, other.configurable) == false) {
            return false;
        }

        if (Utilities.areEqual(this.serviceSpecCharRelationship, other.serviceSpecCharRelationship) == false) {
            return false;
        }

        if (Utilities.areEqual(this.serviceSpecCharacteristicValue, other.serviceSpecCharacteristicValue) == false) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "ServiceSpecCharacteristic{" + "id=" + id + ", name=" + name + ", description=" + description + ", validFor=" + validFor + ", valueType=" + valueType
                + ", configurable=" + configurable + ", serviceSpecCharRelationship=" + serviceSpecCharRelationship + ", serviceSpecCharacteristicValue="
                + serviceSpecCharacteristicValue + '}';
    }

    @JsonIgnore
    public boolean isValid() {
        if (validateCharacteristicValues() == false) {
            return false;
        }

        return true;
    }

    private boolean validateCharacteristicValues() {
        if (Utilities.hasContents(this.serviceSpecCharacteristicValue) == false) {
            return true;
        }

        int defaultCount = 0;
        for (SpecificationCharacteristicValue characteristicValue : this.serviceSpecCharacteristicValue) {
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