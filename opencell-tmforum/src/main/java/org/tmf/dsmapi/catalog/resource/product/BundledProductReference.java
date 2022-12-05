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

package org.tmf.dsmapi.catalog.resource.product;

import java.io.Serializable;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

//import org.tmf.dsmapi.catalog.client.CatalogClient;
import org.tmf.dsmapi.catalog.resource.AbstractEntity;
import org.tmf.dsmapi.catalog.resource.LifecycleStatus;
import org.tmf.dsmapi.commons.AbstractEntityReference;
import org.tmf.dsmapi.commons.Utilities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * 
 * @author bahman.barzideh
 * 
 *         The prefix 'referenced' was added to the property names of this class to work around an issue in the platform. Without the prefix, you could not update the id field of
 *         entity properties that were of this class. For example, attempting to update or edit the ProductSpecification.bundledProductSpecification[n].id would throw an exception.
 *         The exception would claim the operation was attempting to update a key field (the real key field is named ENTITY_ID in the database). The 'referenced' prefix fixes this
 *         issue while making this class a bit uglier than it needs to be.
 * 
 */
@XmlRootElement(name="BundledProductReference", namespace="http://www.tmforum.org")
@XmlType(name="BundledProductReference", namespace="http://www.tmforum.org")
@JsonInclude(value = Include.NON_NULL)
public class BundledProductReference extends AbstractEntityReference implements Serializable {
    public final static long serialVersionUID = 1L;

    @JsonProperty(value = "id")
    private String referencedId;

    @JsonProperty(value = "href")
    private String referencedHref;

    @JsonProperty(value = "name")
    private String referencedName;

    @JsonProperty(value = "lifecycleStatus")
    private LifecycleStatus referencedLifecycleStatus;

    @JsonUnwrapped
    private AbstractEntity entity;

    public BundledProductReference() {
        entity = null;
    }

    public String getReferencedId() {
        return referencedId;
    }

    public void setReferencedId(String referencedId) {
        this.referencedId = referencedId;
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

    public LifecycleStatus getReferencedLifecycleStatus() {
        return referencedLifecycleStatus;
    }

    public void setReferencedLifecycleStatus(LifecycleStatus referencedLifecycleStatus) {
        this.referencedLifecycleStatus = referencedLifecycleStatus;
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

    @JsonProperty(value = "href")
    public String hrefToJson() {
        return (entity == null) ? referencedHref : null;
    }

    @JsonProperty(value = "name")
    public String nameToJson() {
        return (entity == null) ? referencedName : null;
    }

    @JsonProperty(value = "lifecycleStatus")
    public LifecycleStatus lifecycleStatusToJson() {
        return (entity == null) ? referencedLifecycleStatus : null;
    }

    @Override
    public int hashCode() {
        int hash = 7;

        hash = 79 * hash + (this.referencedId != null ? this.referencedId.hashCode() : 0);
        hash = 79 * hash + (this.referencedHref != null ? this.referencedHref.hashCode() : 0);
        hash = 79 * hash + (this.referencedName != null ? this.referencedName.hashCode() : 0);
        hash = 79 * hash + (this.referencedLifecycleStatus != null ? this.referencedLifecycleStatus.hashCode() : 0);

        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        final BundledProductReference other = (BundledProductReference) object;
        if (Utilities.areEqual(this.referencedId, other.referencedId) == false) {
            return false;
        }

        if (Utilities.areEqual(this.referencedHref, other.referencedHref) == false) {
            return false;
        }

        if (Utilities.areEqual(this.referencedName, other.referencedName) == false) {
            return false;
        }

        if (Utilities.areEqual(this.referencedLifecycleStatus, other.referencedLifecycleStatus) == false) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "BundledProductReference{" + "referencedId=" + referencedId + ", referencedHref=" + referencedHref + ", referencedName=" + referencedName
                + ", referencedLifecycleStatus=" + referencedLifecycleStatus + ", entity=" + entity + '}';
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void fetchEntity(Class theClass, int depth) {
        // entity = (AbstractEntity) CatalogClient.getObject(referencedHref, theClass, depth);
    }

    public static BundledProductReference createProto() {
        BundledProductReference bundledProductReference = new BundledProductReference();

        bundledProductReference.referencedId = "id";
        bundledProductReference.referencedHref = "href";
        bundledProductReference.referencedName = "name";
        bundledProductReference.referencedLifecycleStatus = LifecycleStatus.ACTIVE;
        bundledProductReference.entity = null;

        return bundledProductReference;
    }

}
