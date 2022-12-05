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

package org.meveo.api.dto.response;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * The Class SubscriptionWithCreditLimitResponse.
 *
 * @author Edward P. Legaspi
 * @since Nov 13, 2013
 */
@XmlRootElement(name = "SubscriptionWithCreditLimitResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class SubscriptionWithCreditLimitResponse extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 521078357548690693L;

    /** The request id. */
    private String requestId;
    
    /** The accepted. */
    private Boolean accepted;
    
    /** The subscription id. */
    private String subscriptionId;
    
    /** The status. */
    private String status;

    /**
     * Gets the request id.
     *
     * @return the request id
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Sets the request id.
     *
     * @param requestId the new request id
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     * Gets the accepted.
     *
     * @return the accepted
     */
    public Boolean getAccepted() {
        return accepted;
    }

    /**
     * Sets the accepted.
     *
     * @param accepted the new accepted
     */
    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
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
     * Gets the status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status the new status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "SubscriptionWithCreditLimitResponse [requestId=" + requestId + ", accepted=" + accepted + ", subscriptionId=" + subscriptionId + ", status=" + status
                + ", toString()=" + super.toString() + "]";
    }
}