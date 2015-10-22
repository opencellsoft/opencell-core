package org.tmf.dsmapi.catalog.resource;

import java.io.Serializable;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.tmf.dsmapi.commons.Utilities;

/**
 *
 * @author bahman.barzideh
 *
 * {
 *     "role": "Owner",
 *     "id": "1234",
 *     "href": "http ://serverLocation:port/partyManagement/partyRole/1234"
 * }
 *
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Embeddable
public class RelatedParty implements Serializable {
    private final static long serialVersionUID = 1L;

    @Column(name = "REL_PARTY_ID", nullable = true)
    private String id;

    @Column(name = "REL_PARTY_HREF", nullable = true)
    private String href;

    @Column(name = "REL_PARTY_NAME", nullable = true)
    private String name;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "startDateTime", column = @Column(name = "REL_PARTY_START_DATE_TIME")),
        @AttributeOverride(name = "endDateTime", column = @Column(name = "REL_PARTY_END_DATE_TIME"))
    })
    private TimeRange validFor;

    @Column(name = "REL_PARTY_ROLE", nullable = true)
    @JsonProperty(value = "role")
    private String partyRole;

    public RelatedParty() {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TimeRange getValidFor() {
        return validFor;
    }

    public void setValidFor(TimeRange validFor) {
        this.validFor = validFor;
    }

    public String getPartyRole() {
        return partyRole;
    }

    public void setPartyRole(String partyRole) {
        this.partyRole = partyRole;
    }

    @JsonProperty(value = "validFor")
    public TimeRange validForToJson() {
        return (validFor != null && validFor.isEmpty() == false) ? validFor : null;
    }

    @Override
    public int hashCode() {
        int hash = 5;

        hash = 23 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 23 * hash + (this.href != null ? this.href.hashCode() : 0);
        hash = 23 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 23 * hash + (this.validFor != null ? this.validFor.hashCode() : 0);
        hash = 23 * hash + (this.partyRole != null ? this.partyRole.hashCode() : 0);

        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        final RelatedParty other = (RelatedParty) object;
        if (Utilities.areEqual(this.id, other.id) == false) {
            return false;
        }

        if (Utilities.areEqual(this.href, other.href) == false) {
            return false;
        }

        if (Utilities.areEqual(this.name, other.name) == false) {
            return false;
        }

        if (Utilities.areEqual(this.validFor, other.validFor) == false) {
            return false;
        }

        if (Utilities.areEqual(this.partyRole, other.partyRole) == false) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "RelatedParty{" + "id=" + id + ", href=" + href + ", name=" + name + ", validFor=" + validFor + ", partyRole=" + partyRole + '}';
    }

    public static RelatedParty createProto() {
        RelatedParty relatedParty = new RelatedParty();

        relatedParty.id = "id";
        relatedParty.href = "href";
        relatedParty.name = "name";
        relatedParty.validFor = TimeRange.createProto();
        relatedParty.partyRole = "role";

        return relatedParty;
    }

}
