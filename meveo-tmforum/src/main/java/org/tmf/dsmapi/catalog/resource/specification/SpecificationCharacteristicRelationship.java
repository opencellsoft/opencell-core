package org.tmf.dsmapi.catalog.resource.specification;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.tmf.dsmapi.catalog.resource.TimeRange;
import org.tmf.dsmapi.commons.Utilities;

/**
 *
 * @author bahman.barzideh
 *
 * {
 *     "id": "23",
 *     "validFor": {
 *         "startDateTime": "2013-04-19T16:42:23-04:00",
 *         "endDateTime": ""
 *     },
 *     "type": "dependency"
 * }
 *
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Embeddable
public class SpecificationCharacteristicRelationship implements Serializable {
    public final static long serialVersionUID = 1L;

    @Column(name = "ID", nullable = false)
    private String id;

    private TimeRange validFor;

    @Column(name = "TYPE", nullable = true)
    private SpecificationCharacteristicRelationshipType type;

    public SpecificationCharacteristicRelationship() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TimeRange getValidFor() {
        return validFor;
    }

    public void setValidFor(TimeRange validFor) {
        this.validFor = validFor;
    }

    public SpecificationCharacteristicRelationshipType getType() {
        return type;
    }

    public void setType(SpecificationCharacteristicRelationshipType type) {
        this.type = type;
    }

    @JsonProperty(value = "validFor")
    public TimeRange validForToJson() {
        return (validFor != null && validFor.isEmpty() == false) ? validFor : null;
    }

    @Override
    public int hashCode() {
        int hash = 3;

        hash = 79 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 79 * hash + (this.validFor != null ? this.validFor.hashCode() : 0);
        hash = 79 * hash + (this.type != null ? this.type.hashCode() : 0);

        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        final SpecificationCharacteristicRelationship other = (SpecificationCharacteristicRelationship) object;
        if (Utilities.areEqual(this.id, other.id) == false) {
            return false;
        }

        if (Utilities.areEqual(this.validFor, other.validFor) == false) {
            return false;
        }

        if (this.type != other.type) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "SpecificationCharacteristicRelationship{" + "id=" + id + ", validFor=" + validFor + ", type=" + type + '}';
    }

    @JsonIgnore
    public boolean isValid() {
        return true;
    }

    public static SpecificationCharacteristicRelationship createProto() {
        SpecificationCharacteristicRelationship specificationCharacteristicRelationship = new SpecificationCharacteristicRelationship();

        specificationCharacteristicRelationship.id = "id";
        specificationCharacteristicRelationship.validFor = TimeRange.createProto();
        specificationCharacteristicRelationship.type = SpecificationCharacteristicRelationshipType.DEPENDENCY;

        return specificationCharacteristicRelationship;
    }

}