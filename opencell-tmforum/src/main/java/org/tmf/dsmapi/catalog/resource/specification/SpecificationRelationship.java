package org.tmf.dsmapi.catalog.resource.specification;

import java.io.Serializable;

import org.tmf.dsmapi.catalog.resource.TimeRange;
import org.tmf.dsmapi.commons.Utilities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author bahman.barzideh
 * 
 *         { "id": "23", "href": " http://serverlocation:port/catalogManagement/resourceSpecification/23", "validFor": { "startDateTime": "2013-04-19T16:42:23-04:00",
 *         "endDateTime": "" }, "type": "dependency" }
 * 
 */
@JsonInclude(value = Include.NON_NULL)
public class SpecificationRelationship implements Serializable {
    public final static long serialVersionUID = 1L;

    private String id;

    private String href;

    private TimeRange validFor;

    private SpecificationRelationshipType type;

    public SpecificationRelationship() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public TimeRange getValidFor() {
        return validFor;
    }

    public void setValidFor(TimeRange validFor) {
        this.validFor = validFor;
    }

    public SpecificationRelationshipType getType() {
        return type;
    }

    public void setType(SpecificationRelationshipType type) {
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
        hash = 79 * hash + (this.href != null ? this.href.hashCode() : 0);
        hash = 79 * hash + (this.validFor != null ? this.validFor.hashCode() : 0);
        hash = 79 * hash + (this.type != null ? this.type.hashCode() : 0);

        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        final SpecificationRelationship other = (SpecificationRelationship) object;
        if (Utilities.areEqual(this.id, other.id) == false) {
            return false;
        }

        if (Utilities.areEqual(this.href, other.href) == false) {
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
        return "SpecificationRelationship{" + "id=" + id + ", href=" + href + ", validFor=" + validFor + ", type=" + type + '}';
    }

    @JsonIgnore
    public boolean isValid() {
        return true;
    }
}