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

package org.tmf.dsmapi.catalog.resource.resource;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

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
 *         { "id": "42", "version": "2.8", "href": "http://serverlocation:port/catalogManagement/resourceCandidate/42", "name": "Virtual Storage Medium", "description":
 *         "Virtual Storage Medium", "lastUpdate": "2013-04-19T16:42:23-04:00", "lifecycleStatus": "Active", "validFor": { "startDateTime": "2013-04-19T16:42:23-04:00",
 *         "endDateTime": "2013-06-19T00:00:00-04:00" }, "category": [ { "id": "12", "href": "http://serverlocation:port/catalogManagement/resourceCategory/12", "version": "2.0",
 *         "name": "Cloud offerings" } ], "serviceLevelAgreement": { "id": "28", "href": "http://serverlocation:port/slaManagement/serviceLevelAgreement/28", "name": "Standard SLA"
 *         }, "resourceSpecification": { "id": "13", "href": "http://serverlocation:port/catalogManagement/resourceSpecification/13", "name": "specification 1", "version": "1.1" }
 *         }
 * 
 */
@XmlRootElement(name="ResourceCandidate", namespace="http://www.tmforum.org")
@XmlType(name="ResourceCandidate", namespace="http://www.tmforum.org")
@JsonInclude(value = Include.NON_NULL)
public class ResourceCandidate extends AbstractCatalogEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private final static Logger logger = Logger.getLogger(ResourceCandidate.class.getName());

    private List<CatalogReference> category;

    private ServiceLevelAgreement serviceLevelAgreement;

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
        return "ResourceCandidate{<" + super.toString() + ">, category=" + category + ", serviceLevelAgreement=" + serviceLevelAgreement + ", resourceSpecification="
                + resourceSpecification + '}';
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
}