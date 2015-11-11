package org.tmf.dsmapi.catalog.resource.service;

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
 *     "version": "2.1",
 *     "href": "http://serverlocation:port/catalogManagement/serviceCandidate/42",
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
 *             "version": "2.2",
 *             "href": "http://serverlocation:port/catalogManagement/category/12",
 *             "name": "Cloud service"
 *         }
 *     ],
 *     "serviceLevelAgreement": {
 *         "id": "28",
 *         "href": "http://serverlocation:port/slaManagement/serviceLevelAgreement/28",
 *         "name": "Standard SLA"
 *     },
 *     "serviceSpecification": {
 *         "id": "13",
 *         "version": "1.2",
 *         "href": "http://serverlocation:port/catalogManagement/serviceSpecification/13",
 *         "name": "specification 1"
 *     }
 * }
 *
 */
@MappedSuperclass
@XmlRootElement
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServiceCandidate extends AbstractCatalogEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private final static Logger logger = Logger.getLogger(ServiceCandidate.class.getName());

    @Embedded
    @ElementCollection
    @CollectionTable(name = "CRI_SERVICE_R_CATEGORY", joinColumns = {
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
        @AttributeOverride(name = "referencedId", column = @Column(name = "SERVICE_SPEC_ID")),
        @AttributeOverride(name = "referencedVersion", column = @Column(name = "SERVICE_SPEC_VERSION")),
        @AttributeOverride(name = "referencedHref", column = @Column(name = "SERVICE_SPEC_HREF")),
        @AttributeOverride(name = "referencedName", column = @Column(name = "SERVICE_SPEC_NAME")),
        @AttributeOverride(name = "referencedDescription", column = @Column(name = "SERVICE_SPEC_DESCRIPTION"))
    })
    @EntityReferenceProperty(classId=ServiceSpecification.class)
    private CatalogReference serviceSpecification;

    public ServiceCandidate() {
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

    public CatalogReference getServiceSpecification() {
        return serviceSpecification;
    }

    public void setServiceSpecification(CatalogReference serviceSpecification) {
        this.serviceSpecification = serviceSpecification;
    }

    @JsonProperty(value = "category")
    public List<CatalogReference> categoryToJson() {
        return (category != null && category.size() > 0) ? category : null;
    }

    @Override
    public int hashCode() {
        int hash = 7;

        hash = 17 * hash + super.hashCode();

        hash = 17 * hash + (this.category != null ? this.category.hashCode() : 0);
        hash = 17 * hash + (this.serviceLevelAgreement != null ? this.serviceLevelAgreement.hashCode() : 0);
        hash = 17 * hash + (this.serviceSpecification != null ? this.serviceSpecification.hashCode() : 0);

        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass() || super.equals(object) == false) {
            return false;
        }

        final ServiceCandidate other = (ServiceCandidate) object;
        if (Utilities.areEqual(this.category, other.category) == false) {
            return false;
        }

        if (Utilities.areEqual(this.serviceLevelAgreement, other.serviceLevelAgreement) == false) {
            return false;
        }

        if (Utilities.areEqual(this.serviceSpecification, other.serviceSpecification) == false) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "ServiceCandidate{<" + super.toString() + ">, category=" + category + ", serviceLevelAgreement=" + serviceLevelAgreement + ", serviceSpecification=" + serviceSpecification + '}';
    }

    @Override
    @JsonIgnore
    public Logger getLogger() {
        return logger;
    }

    public void edit(ServiceCandidate input) {
        if (input == null || input == this) {
            return;
        }

        super.edit(input);

        if (this.category == null) {
            this.category = input.category;
        }

        if (this.serviceLevelAgreement == null) {
            this.serviceLevelAgreement = input.serviceLevelAgreement;
        }

        if (this.serviceSpecification == null) {
            this.serviceSpecification = input.serviceSpecification;
        }
    }

    @Override
    @JsonIgnore
    public boolean isValid() {
        logger.log(Level.FINE, "ServiceCandidate:valid ()");

        if (super.isValid() == false) {
            return false;
        }

        return true;
    }

    public static ServiceCandidate createProto() {
        ServiceCandidate serviceCandidate = new ServiceCandidate();

        serviceCandidate.setId("id");
        serviceCandidate.setVersion("1.3");
        serviceCandidate.setHref("href");
        serviceCandidate.setName("name");
        serviceCandidate.setDescription("description");
        serviceCandidate.setLastUpdate(new Date ());
        serviceCandidate.setLifecycleStatus(LifecycleStatus.ACTIVE);
        serviceCandidate.setValidFor(TimeRange.createProto ());

        serviceCandidate.category = new ArrayList<CatalogReference>();
        serviceCandidate.category.add(CatalogReference.createProto());

        serviceCandidate.serviceLevelAgreement = ServiceLevelAgreement.createProto();
        serviceCandidate.serviceSpecification = CatalogReference.createProto();

        return serviceCandidate;
    }

}
