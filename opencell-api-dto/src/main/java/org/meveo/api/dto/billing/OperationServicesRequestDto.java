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

package org.meveo.api.dto.billing;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;

/**
 * The Class OperationServicesRequestDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "OperationServicesRequestDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class OperationServicesRequestDto extends BaseEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1150993171011072506L;

    /** The subscription code. */
    @XmlElement(required = true)
    private String subscriptionCode;
    private Date subscriptionValidityDate;

    /** The services to update. */
    @XmlElementWrapper(name = "ListServiceToUpdate")
    @XmlElement(name = "serviceToUpdate")
    private List<ServiceToUpdateDto> servicesToUpdate = new ArrayList<ServiceToUpdateDto>();

    /**
     * Gets the subscription code.
     *
     * @return the subscription code
     */
    public String getSubscriptionCode() {
        return subscriptionCode;
    }

    /**
     * Sets the subscription code.
     *
     * @param subscriptionCode the new subscription code
     */
    public void setSubscriptionCode(String subscriptionCode) {
        this.subscriptionCode = subscriptionCode;
    }

    /**
     * Gets the services to update.
     *
     * @return the services to update
     */
    public List<ServiceToUpdateDto> getServicesToUpdate() {
        return servicesToUpdate;
    }

    /**
     * Sets the services to update.
     *
     * @param servicesToUpdate the new services to update
     */
    public void setServicesToUpdate(List<ServiceToUpdateDto> servicesToUpdate) {
        this.servicesToUpdate = servicesToUpdate;
    }

    public void setSubscriptionValidityDate(Date subscriptionValidityDate) {
        this.subscriptionValidityDate = subscriptionValidityDate;
    }

    public Date getSubscriptionValidityDate() {
        return this.subscriptionValidityDate;
    }
    @Override
    public String toString() {
        return "OperationServicesRequestDto [subscriptionCode=" + subscriptionCode + ", servicesToUpdate=" + servicesToUpdate + "]";
    }
}

