package org.tmf.dsmapi.catalog.resource.product;

import java.io.Serializable;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.tmf.dsmapi.catalog.resource.TimeRange;
import org.tmf.dsmapi.commons.Utilities;

/**
 *
 * @author bahman.barzideh
 *
 * {
 *     "name": "12 Month",
 *     "description": "12 month contract",
 *     "duration": "12",
 *     "validFor": {
 *         "startDateTime": "2013-04-19T16:42:23-04:00",
 *         "endDateTime": "2013-06-19T00:00:00-04:00"
 *     }
 * }
 *
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Embeddable
public class ProductOfferingTerm implements Serializable {
    private final static long serialVersionUID = 1L;

    @Column(name = "OFFERING_TERM_NAME", nullable = true)
    private String name;

    @Column(name = "OFFERING_TERM_DESCRIPTION", nullable = true)
    private String description;

    @Column(name = "OFFERING_TERM_DURATION", nullable = true)
    private String duration;

    @AttributeOverrides({
        @AttributeOverride(name = "startDateTime", column = @Column(name = "OFFERING_TERM_START_DATE_TIME")),
        @AttributeOverride(name = "endDateTime", column = @Column(name = "OFFERING_TERM_END_DATE_TIME"))
    })
    private TimeRange validFor;

    public ProductOfferingTerm() {
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

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public TimeRange getValidFor() {
        return validFor;
    }

    public void setValidFor(TimeRange validFor) {
        this.validFor = validFor;
    }

    @Override
    public int hashCode() {
        int hash = 3;

        hash = 53 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 53 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 53 * hash + (this.duration != null ? this.duration.hashCode() : 0);
        hash = 53 * hash + (this.validFor != null ? this.validFor.hashCode() : 0);

        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        final ProductOfferingTerm other = (ProductOfferingTerm) object;
        if (Utilities.areEqual(this.name, other.name) == false) {
            return false;
        }

        if (Utilities.areEqual(this.description, other.description) == false) {
            return false;
        }

        if (Utilities.areEqual(this.duration, other.duration) == false) {
            return false;
        }

        if (Utilities.areEqual(this.validFor, other.validFor) == false) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "ProductOfferingTerm{" + "name=" + name + ", description=" + description + ", duration=" + duration + ", validFor=" + validFor + '}';
    }

    public static ProductOfferingTerm createProto() {
        ProductOfferingTerm productOfferingTerm = new ProductOfferingTerm();

        productOfferingTerm.name = "name";
        productOfferingTerm.description = "description";
        productOfferingTerm.duration = "12";
        productOfferingTerm.validFor = TimeRange.createProto();

        return productOfferingTerm;
    }

}
