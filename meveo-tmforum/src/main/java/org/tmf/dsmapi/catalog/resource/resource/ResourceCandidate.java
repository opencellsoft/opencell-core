package org.tmf.dsmapi.catalog.resource.resource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.tmf.dsmapi.catalog.resource.AbstractCatalogEntity;
import org.tmf.dsmapi.catalog.resource.CatalogReference;
import org.tmf.dsmapi.catalog.resource.LifecycleStatus;
import org.tmf.dsmapi.catalog.resource.ServiceLevelAgreement;
import org.tmf.dsmapi.catalog.resource.TimeRange;
import org.tmf.dsmapi.catalog.resource.category.Category;
import org.tmf.dsmapi.commons.Utilities;
import org.tmf.dsmapi.commons.annotation.EntityReferenceProperty;

/**
 *
 * @author bahman.barzideh
 *
 * {
 *     "id": "42",
 *     "version": "2.8",
 *     "href": "http://serverlocation:port/catalogManagement/resourceCandidate/42",
 *     "name": "Virtual Storage Medium",
 *     "description": "Virtual Storage Medium",
 *     "lastUpdate": "2013-04-19T16:42:23-04:00",
 *     "lifecycleStatus": "Active",
 *     "validFor": {
 *         "startDateTime": "2013-04-19T16:42:23-04:00",
 *         "endDateTime": "2013-06-19T00:00:00-04:00"
 *     },
 *     "category": [
 *         {
 *             "id": "12",
 *             "href": "http://serverlocation:port/catalogManagement/resourceCategory/12",
 *             "version": "2.0",
 *             "name": "Cloud offerings"
 *         }
 *     ],
 *     "serviceLevelAgreement": {
 *         "id": "28",
 *         "href": "http://serverlocation:port/slaManagement/serviceLevelAgreement/28",
 *         "name": "Standard SLA"
 *     },
 *     "resourceSpecification": {
 *         "id": "13",
 *         "href": "http://serverlocation:port/catalogManagement/resourceSpecification/13",
 *         "name": "specification 1",
 *         "version": "1.1"
 *     }
 * }
 *
 */
@MappedSuperclass
@XmlRootElement
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ResourceCandidate extends AbstractCatalogEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private final static Logger logger = Logger.getLogger(ResourceCandidate.class.getName());

    @Embedded
    @ElementCollection
    @CollectionTable(name = "CRI_RESOURCE_R_CATEGORY", joinColumns = {
        @JoinColumn(name = "CATALOG_ID", referencedColumnName = "CATALOG_ID"),
        @JoinColumn(name = "CATALOG_VERSION", referencedColumnName = "CATALOG_VERSION"),
        @JoinColumn(name = "ENTITY_ID", referencedColumnName = "ID"),
        @JoinColumn(name = "ENTITY_VERSION", referencedColumnName = "VERSION")
    })
    @EntityReferenceProperty(classId=Category.class)
    private List<CatalogReference> category;

    @Embedded
    private ServiceLevelAgreement serviceLevelAgreement;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "referencedId", column = @Column(name = "RESOURCE_SPEC_ID")),
        @AttributeOverride(name = "referencedVersion", column = @Column(name = "RESOURCE_SPEC_VERSION")),
        @AttributeOverride(name = "referencedHref", column = @Column(name = "RESOURCE_SPEC_HREF")),
        @AttributeOverride(name = "referencedName", column = @Column(name = "RESOURCE_SPEC_NAME")),
        @AttributeOverride(name = "referencedDescription", column = @Column(name = "RESOURCE_SPEC_DESCRIPTION"))
    })
    @EntityReferenceProperty(classId=ResourceSpecification.class)
    private CatalogReference resourceSpecification;

    public ResourceCandidate() {
    }

    public List<CatalogReference> getCategory() {
        return category;
    }

    public void setCategory(List<CatalogReference> category) {
        this.category = category;
    }

    public ServiceLevelAgreement getServiceLevelAgreement() {
        return serviceLevelAgreement;
    }

    public void setServiceLevelAgreement(ServiceLevelAgreement serviceLevelAgreement) {
        this.serviceLevelAgreement = serviceLevelAgreement;
    }

    public CatalogReference getResourceSpecification() {
        return resourceSpecification;
    }

    public void setResourceSpecification(CatalogReference resourceSpecification) {
        this.resourceSpecification = resourceSpecification;
    }

    @JsonProperty(value = "category")
    public List<CatalogReference> categoryToJson() {
        return (category != null && category.size() > 0) ? category : null;
    }

    @Override
    public int hashCode() {
        int hash = 7;

        hash = 97 * hash + super.hashCode();

        hash = 97 * hash + (this.category != null ? this.category.hashCode() : 0);
        hash = 97 * hash + (this.serviceLevelAgreement != null ? this.serviceLevelAgreement.hashCode() : 0);
        hash = 97 * hash + (this.resourceSpecification != null ? this.resourceSpecification.hashCode() : 0);

        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass() || super.equals(object) == false) {
            return false;
        }

        final ResourceCandidate other = (ResourceCandidate) object;
        if (Utilities.areEqual(this.category, other.category) == false) {
            return false;
        }

        if (Utilities.areEqual(this.serviceLevelAgreement, other.serviceLevelAgreement) == false) {
            return false;
        }

        if (Utilities.areEqual(this.resourceSpecification, other.resourceSpecification) == false) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "ResourceCandidate{<" + super.toString() + ">, category=" + category + ", serviceLevelAgreement=" + serviceLevelAgreement + ", resourceSpecification=" + resourceSpecification + '}';
    }

    @Override
    @JsonIgnore
    public Logger getLogger() {
        return logger;
    }

    public void edit(ResourceCandidate input) {
        if (this == null || input == this) {
            return;
        }

        super.edit(input);

        if (this.category == null) {
            this.category = input.category;
        }

        if (this.serviceLevelAgreement == null) {
            this.serviceLevelAgreement = input.serviceLevelAgreement;
        }

        if (this.resourceSpecification == null) {
            this.resourceSpecification = input.resourceSpecification;
        }
    }

    @Override
    @JsonIgnore
    public boolean isValid() {
        logger.log(Level.FINE, "ResourceCandidate:valid ()");

        if (super.isValid() == false) {
            return false;
        }

        return true;
    }

    public static ResourceCandidate createProto() {
        ResourceCandidate resourceCandidate = new ResourceCandidate();

        resourceCandidate.setId("id");
        resourceCandidate.setVersion("1.3");
        resourceCandidate.setHref("href");
        resourceCandidate.setName("name");
        resourceCandidate.setDescription("description");
        resourceCandidate.setLastUpdate(new Date ());
        resourceCandidate.setLifecycleStatus(LifecycleStatus.ACTIVE);
        resourceCandidate.setValidFor(TimeRange.createProto ());

        resourceCandidate.category = new ArrayList<CatalogReference>();
        resourceCandidate.category.add(CatalogReference.createProto());

        resourceCandidate.serviceLevelAgreement = ServiceLevelAgreement.createProto();
        resourceCandidate.resourceSpecification = CatalogReference.createProto();

        return resourceCandidate;
    }

}
