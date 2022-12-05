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

package org.meveo.api.dto;

import java.util.Date;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * The Class SubscriptionWithCreditLimitDto.
 *
 * @author Edward P. Legaspi
 * @since Nov 13, 2013
 */
@XmlRootElement(name = "SubscriptionWithCreditLimit")
@XmlAccessorType(XmlAccessType.FIELD)
public class SubscriptionWithCreditLimitDto extends BaseEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6700315102709912658L;

    /** The user id. */
    private String userId; // unused
    
    /** The organization id. */
    private String organizationId;
    
    /** The offer id. */
    private String offerId;
    
    /** The services to add. */
    private List<ServiceToAddDto> servicesToAdd;
    
    /** The credit limits. */
    private List<CreditLimitDto> creditLimits;
    
    /** The subscription date. */
    private Date subscriptionDate;

    /**
     * Gets the user id.
     *
     * @return the user id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user id.
     *
     * @param userId the new user id
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the organization id.
     *
     * @return the organization id
     */
    public String getOrganizationId() {
        return organizationId;
    }

    /**
     * Sets the organization id.
     *
     * @param organizationId the new organization id
     */
    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    /**
     * Gets the offer id.
     *
     * @return the offer id
     */
    public String getOfferId() {
        return offerId;
    }

    /**
     * Sets the offer id.
     *
     * @param offerId the new offer id
     */
    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    /**
     * Gets the services to add.
     *
     * @return the services to add
     */
    public List<ServiceToAddDto> getServicesToAdd() {
        return servicesToAdd;
    }

    /**
     * Sets the services to add.
     *
     * @param servicesToAdd the new services to add
     */
    public void setServicesToAdd(List<ServiceToAddDto> servicesToAdd) {
        this.servicesToAdd = servicesToAdd;
    }

    /**
     * Gets the credit limits.
     *
     * @return the credit limits
     */
    public List<CreditLimitDto> getCreditLimits() {
        return creditLimits;
    }

    /**
     * Sets the credit limits.
     *
     * @param creditLimits the new credit limits
     */
    public void setCreditLimits(List<CreditLimitDto> creditLimits) {
        this.creditLimits = creditLimits;
    }

    /**
     * Gets the subscription date.
     *
     * @return the subscription date
     */
    public Date getSubscriptionDate() {
        return subscriptionDate;
    }

    /**
     * Sets the subscription date.
     *
     * @param subscriptionDate the new subscription date
     */
    public void setSubscriptionDate(Date subscriptionDate) {
        this.subscriptionDate = subscriptionDate;
    }
}