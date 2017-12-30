package org.tmf.dsmapi.catalog.resource;

import java.io.Serializable;

//import org.tmf.dsmapi.catalog.client.CatalogClient;
import org.tmf.dsmapi.commons.AbstractEntityReference;
import org.tmf.dsmapi.commons.ParsedVersion;
import org.tmf.dsmapi.commons.Utilities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * 
 * @author bahman.barzideh
 * 
 *         The prefix 'referenced' was added to the property names of this class to work around an issue in the platform. Without the prefix, you could not update the id &amp; version
 *         fields of entity properties that were of this class. For example, attempting to update or edit the ResourceCandidate.category[n].version would throw an exception. The
 *         exception would claim the operation was attempting to update a key field (the real key field is named ENTITY_VERSION in the database). The 'referenced' prefix fixes this
 *         issue while making this class a bit uglier than it needs to be.
 * 
 */
@JsonInclude(value = Include.NON_NULL)
public class CatalogReference extends AbstractEntityReference implements Serializable {
    public final static long serialVersionUID = 1L;

    @JsonProperty(value = "id")
    private String referencedId;

    @JsonProperty(value = "version")
    private String referencedVersion;

    @JsonIgnore
    private ParsedVersion parsedVersion;

    @JsonProperty(value = "href")
    private String referencedHref;

    @JsonProperty(value = "name")
    private String referencedName;

    @JsonProperty(value = "description")
    private String referencedDescription;

    @JsonUnwrapped
    private AbstractEntity entity;

    public CatalogReference() {
        entity = null;
    }

    public String getReferencedId() {
        return referencedId;
    }

    public void setReferencedId(String referencedId) {
        this.referencedId = referencedId;
    }

    public String getReferencedVersion() {
        return referencedVersion;
    }

    public void setReferencedVersion(String referencedVersion) {
        if (referencedVersion == null) {
            this.referencedVersion = null;
            this.parsedVersion = null;
            return;
        }

        this.parsedVersion = new ParsedVersion(referencedVersion);
        this.referencedVersion = this.parsedVersion.getInternalView();
    }

    public ParsedVersion getParsedVersion() {
        if (parsedVersion == null && referencedVersion != null) {
            parsedVersion = new ParsedVersion(referencedVersion);
        }

        return parsedVersion;
    }

    public String getReferencedHref() {
        return referencedHref;
    }

    public void setReferencedHref(String referencedHref) {
        this.referencedHref = referencedHref;
    }

    public String getReferencedName() {
        return referencedName;
    }

    public void setReferencedName(String referencedName) {
        this.referencedName = referencedName;
    }

    public String getReferencedDescription() {
        return referencedDescription;
    }

    public void setReferencedDescription(String referencedDescription) {
        this.referencedDescription = referencedDescription;
    }

    public AbstractEntity getEntity() {
        return entity;
    }

    public void setEntity(AbstractEntity entity) {
        this.entity = entity;
    }

    @JsonProperty(value = "id")
    public String idToJson() {
        return (entity == null) ? referencedId : null;
    }

    @JsonProperty(value = "version")
    public String versionToJson() {
        if (entity != null) {
            return null;
        }

        ParsedVersion theParsedVersion = getParsedVersion();
        return (theParsedVersion != null) ? theParsedVersion.getExternalView() : null;
    }

    @JsonProperty(value = "href")
    public String hrefToJson() {
        return (entity == null) ? referencedHref : null;
    }

    @JsonProperty(value = "name")
    public String nameToJson() {
        return (entity == null) ? referencedName : null;
    }

    @JsonProperty(value = "description")
    public String descriptionToJson() {
        return (entity == null) ? referencedDescription : null;
    }

    @Override
    public int hashCode() {
        int hash = 5;

        hash = 53 * hash + (this.referencedId != null ? this.referencedId.hashCode() : 0);
        hash = 53 * hash + (this.referencedVersion != null ? this.referencedVersion.hashCode() : 0);
        hash = 53 * hash + (this.referencedHref != null ? this.referencedHref.hashCode() : 0);
        hash = 53 * hash + (this.referencedName != null ? this.referencedName.hashCode() : 0);
        hash = 53 * hash + (this.referencedDescription != null ? this.referencedDescription.hashCode() : 0);

        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        final CatalogReference other = (CatalogReference) object;
        if (Utilities.areEqual(this.referencedId, other.referencedId) == false) {
            return false;
        }

        if (Utilities.areEqual(this.referencedVersion, other.referencedVersion) == false) {
            return false;
        }

        if (Utilities.areEqual(this.referencedHref, other.referencedHref) == false) {
            return false;
        }

        if (Utilities.areEqual(this.referencedName, other.referencedName) == false) {
            return false;
        }

        if (Utilities.areEqual(this.referencedDescription, other.referencedDescription) == false) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "Reference{" + "referencedId=" + referencedId + ", referencedVersion=" + referencedVersion + ", parsedVersion=" + parsedVersion + ", referencedHref="
                + referencedHref + ", referencedName=" + referencedName + ", referencedDescription=" + referencedDescription + ", entity=" + entity + '}';
    }

    @Override
    public void fetchEntity(Class theClass, int depth) {
        // entity = (AbstractEntity) CatalogClient.getObject(referencedHref, theClass, depth);
    }

    public static CatalogReference createProto() {
        CatalogReference catalogReference = new CatalogReference();

        catalogReference.referencedId = "id";
        catalogReference.referencedVersion = "1.6";
        catalogReference.referencedHref = "href";
        catalogReference.referencedName = "name";
        catalogReference.referencedDescription = "description";
        catalogReference.entity = null;

        return catalogReference;
    }

}
