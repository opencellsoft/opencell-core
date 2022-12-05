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

package org.meveo.model.billing;

import java.math.BigDecimal;
import java.util.Date;

import org.meveo.model.catalog.OneShotChargeTemplate;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("S")
public class SubscriptionChargeInstance extends OneShotChargeInstance {

    private static final long serialVersionUID = 9173788298851287042L;

    /**
     * Constructor
     */
    public SubscriptionChargeInstance() {
        super();
    }

    /**
     * Constructor
     * 
     * @param amountWithoutTax Amount without tax
     * @param amountWithTax Amount with tax
     * @param chargeTemplate Charge template
     * @param serviceInstance Service instance to associate with
     * @param status Status
     */
    public SubscriptionChargeInstance(BigDecimal amountWithoutTax, BigDecimal amountWithTax, OneShotChargeTemplate chargeTemplate, ServiceInstance serviceInstance, InstanceStatusEnum status) {
        super(amountWithoutTax, amountWithTax, chargeTemplate, serviceInstance, status);
    }

    /**
     * Constructor
     * 
     * @param description Charge description (to override a value from a charge template). Optional
     * @param chargeDate Charge date
     * @param amountWithoutTax Amount without tax
     * @param amountWithTax Amount with tax
     * @param quantity Quantity
     * @param orderNumberOverride Order number to override. If not provided, a value from service instance will be used. A value of ChargeInstance.NO_ORDER_NUMBER will set a value to null.
     * @param serviceInstance Service instance
     * @param chargeTemplate Charge template
     */
    public SubscriptionChargeInstance(String description, Date chargeDate, BigDecimal amountWithoutTax, BigDecimal amountWithTax, BigDecimal quantity, String orderNumberOverride, ServiceInstance serviceInstance,
            OneShotChargeTemplate chargeTemplate) {
        super(description, chargeDate, amountWithoutTax, amountWithTax, quantity, orderNumberOverride, serviceInstance, chargeTemplate);
    }

    /**
     * Constructor
     * 
     * @param description Charge description (to override a value from a charge template). Optional
     * @param chargeDate Charge date
     * @param amountWithoutTax Amount without tax
     * @param amountWithTax Amount with tax
     * @param quantity Quantity
     * @param orderNumberOverride Order number to override. If not provided, a value from subscription will be used. A value of ChargeInstance.NO_ORDER_NUMBER will set a value to null.
     * @param subscription Subscription
     * @param chargeTemplate Charge template
     */
    public SubscriptionChargeInstance(String description, Date chargeDate, BigDecimal amountWithoutTax, BigDecimal amountWithTax, BigDecimal quantity, String orderNumberOverride, Subscription subscription,
            OneShotChargeTemplate chargeTemplate) {
        super(description,  chargeDate,  amountWithoutTax,  amountWithTax,  quantity,  orderNumberOverride,  subscription,
             chargeTemplate);
    }
}