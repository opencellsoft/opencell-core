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

package org.meveo.api.dto.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.ActionStatus;
import org.meveo.model.payments.PaymentStatusEnum;

/**
 * The Class HostedCheckoutStatusResponseDto.
 *
 * @author anasseh
 */

@XmlRootElement(name = "HostedCheckoutStatusResponseDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class HostedCheckoutStatusResponseDto extends ActionStatus {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -2805352475577374256L;

    /** The HostedCheckoutStatus. "PAYMENT_CREATED" "IN_PROGRESS" "CANCELLED_BY_CONSUMER"*/
    private String hostedCheckoutStatus;
        
    /** The paymentStatus */
    private PaymentStatusEnum paymentStatus;
    
    /** The payment id */
    private String paymentId;
    
    /**
     * Instantiates a new payment gateway response dto.
     */
    public HostedCheckoutStatusResponseDto() {

    }

    /**
     * @return the hostedCheckoutStatus
     */
    public String getHostedCheckoutStatus() {
        return hostedCheckoutStatus;
    }

    /**
     * @param hostedCheckoutStatus the hostedCheckoutStatus to set
     */
    public void setHostedCheckoutStatus(String hostedCheckoutStatus) {
        this.hostedCheckoutStatus = hostedCheckoutStatus;
    }

   

    /**
     * @return the paymentStatus
     */
    public PaymentStatusEnum getPaymentStatus() {
        return paymentStatus;
    }

    /**
     * @param paymentStatus the paymentStatus to set
     */
    public void setPaymentStatus(PaymentStatusEnum paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    /**
     * @return the paymentId
     */
    public String getPaymentId() {
        return paymentId;
    }

    /**
     * @param paymentId the paymentId to set
     */
    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    @Override
    public String toString() {
        return "HostedCheckoutStatusResponseDto [hostedCheckoutStatus=" + hostedCheckoutStatus + ", paymentStatus="
                + paymentStatus + ", paymentId=" + paymentId + "]";
    }
    
}
