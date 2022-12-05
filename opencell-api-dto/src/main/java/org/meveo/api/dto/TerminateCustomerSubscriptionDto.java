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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * The Class TerminateCustomerSubscriptionDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "TerminateSubscription")
@XmlAccessorType(XmlAccessType.FIELD)
public class TerminateCustomerSubscriptionDto extends BaseEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6700315102709912658L;

    /** The user id. */
    private String userId; // unused
    
    /** The organization id. */
    private String organizationId;
    
    /** The subscription id. */
    private String subscriptionId;
    
    /** The termination date. */
    private Date terminationDate;

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
     * Gets the subscription id.
     *
     * @return the subscription id
     */
    public String getSubscriptionId() {
        return subscriptionId;
    }

    /**
     * Sets the subscription id.
     *
     * @param subscriptionId the new subscription id
     */
    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    /**
     * Gets the termination date.
     *
     * @return the termination date
     */
    public Date getTerminationDate() {
        return terminationDate;
    }

    /**
     * Sets the termination date.
     *
     * @param terminationDate the new termination date
     */
    public void setTerminationDate(Date terminationDate) {
        this.terminationDate = terminationDate;
    }
}