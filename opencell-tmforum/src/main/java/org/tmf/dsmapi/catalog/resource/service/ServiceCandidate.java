package org.tmf.dsmapi.catalog.resource.service;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.tmf.dsmapi.catalog.resource.AbstractCatalogEntity;
import org.tmf.dsmapi.catalog.resource.CatalogReference;
import org.tmf.dsmapi.catalog.resource.ServiceLevelAgreement;
import org.tmf.dsmapi.commons.Utilities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author bahman.barzideh
 * 
 *         { "id": "42", "version": "2.1", "href": "http://serverlocation:port/catalogManagement/serviceCandidate/42", "name": "Virtual Storage Medium", "description":
 *         "Virtual Storage Medium", "lastUpdate": "2013-04-19T16:42:23-04:00", "lifecycleStatus": "Active", "validFor": { "startDateTime": "2013-04-19T16:42:23-04:00",
 *         "endDateTime": "2013-06-19T00:00:00-04:00" }, "category": [ { "id": "12", "version": "2.2", "href": "http://serverlocation:port/catalogManagement/category/12", "name":
 *         "Cloud service" } ], "serviceLevelAgreement": { "id": "28", "href": "http://serverlocation:port/slaManagement/serviceLevelAgreement/28", "name": "Standard SLA" },
 *         "serviceSpecification": { "id": "13", "version": "1.2", "href": "http://serverlocation:port/catalogManagement/serviceSpecification/13", "name": "specification 1" } }
 * 
 */
@XmlRootElement(name="ServiceCandidate", namespace="http://www.tmforum.org")
@XmlType(name="ServiceCandidate", namespace="http://www.tmforum.org")
@JsonInclude(value = Include.NON_NULL)
public class ServiceCandidate extends AbstractCatalogEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private final static Logger logger = Logger.getLogger(ServiceCandidate.class.getName());

    private List<CatalogReference> category;

    private ServiceLevelAgreement serviceLevelAgreement;

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
        return "ServiceCandidate{<" + super.toString() + ">, category=" + category + ", serviceLevelAgreement=" + serviceLevelAgreement + ", serviceSpecification="
                + serviceSpecification + '}';
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
}