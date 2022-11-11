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

package org.meveo.api.dto.tunnel;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.subscriptionTunnel.TunnelCustomization;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class TunnelPaymentMethodDto.
 *
 * @author Ilham CHAFIK
 */
@XmlRootElement(name = "TunnelPaymentMethod")
@XmlAccessorType(XmlAccessType.FIELD)
public class TunnelPaymentMethodDto extends BaseEntityDto {

    /**
     * serial version uid.
     */
    private static final long serialVersionUID = -2591197886629041589L;

    /**
     * The payment method.
     */
    private PaymentMethodEnum paymentMethod;


    private TunnelCustomization tunnelCustomization;

    /**
     * Instantiates a new Payment method
     */
    public TunnelPaymentMethodDto() {   }

    /**
     * Instantiates a new Payment method
     * @param paymentMethod the payment method
     * @param tunnelCustomization the tunnel
     */
    public TunnelPaymentMethodDto(PaymentMethodEnum paymentMethod, TunnelCustomization tunnelCustomization) {
        this.paymentMethod = paymentMethod;
        this.tunnelCustomization = tunnelCustomization;
    }

    /**
     * Instantiates a new Payment method
     * @param paymentMethod the payment method
     * @param mandateContract the mandat contract
     * @param tunnelCustomization the tunnel
     */
    public TunnelPaymentMethodDto(PaymentMethodEnum paymentMethod, String mandateContract, TunnelCustomization tunnelCustomization) {
        this.paymentMethod = paymentMethod;
        this.tunnelCustomization = tunnelCustomization;
    }

    /**
     * Gets the payment method.
     * @return the payment method
     */
    public PaymentMethodEnum getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * Sets the payment method.
     * @param paymentMethod the payment method
     */
    public void setPaymentMethod(PaymentMethodEnum paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    /**
     *
     * @return the tunnel
     */
    public TunnelCustomization getTunnelCustomization() {
        return tunnelCustomization;
    }

    /**
     *
     * @param tunnelCustomization the tunnel
     */
    public void setTunnelCustomization(TunnelCustomization tunnelCustomization) {
        this.tunnelCustomization = tunnelCustomization;
    }
}
