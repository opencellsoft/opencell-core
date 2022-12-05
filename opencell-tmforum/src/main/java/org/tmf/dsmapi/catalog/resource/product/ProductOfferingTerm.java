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

import org.tmf.dsmapi.catalog.resource.TimeRange;
import org.tmf.dsmapi.commons.Utilities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 
 * @author bahman.barzideh
 * 
 *         { "name": "12 Month", "description": "12 month contract", "duration": "12", "validFor": { "startDateTime": "2013-04-19T16:42:23-04:00", "endDateTime":
 *         "2013-06-19T00:00:00-04:00" } }
 * 
 */
@XmlRootElement(name="ProductOfferingTerm", namespace="http://www.tmforum.org")
@XmlType(name="ProductOfferingTerm", namespace="http://www.tmforum.org")
@JsonInclude(value = Include.NON_NULL)
public class ProductOfferingTerm implements Serializable {
    private final static long serialVersionUID = 1L;

    private String name;

    private String description;

    private String duration;

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
}