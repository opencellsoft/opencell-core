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

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.meveo.model.catalog.OneShotChargeTemplate;

@Entity
@DiscriminatorValue("T")
public class TerminationChargeInstance extends OneShotChargeInstance {

    private static final long serialVersionUID = 8396486496100011318L;

    /**
     * Constructor
     */
    public TerminationChargeInstance() {
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
    public TerminationChargeInstance(BigDecimal amountWithoutTax, BigDecimal amountWithTax, OneShotChargeTemplate chargeTemplate, ServiceInstance serviceInstance, InstanceStatusEnum status) {
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
     * @param orderNumber Order number
     * @param serviceInstance Service instance
     * @param chargeTemplate Charge template
     */
    public TerminationChargeInstance(String description, Date chargeDate, BigDecimal amountWithoutTax, BigDecimal amountWithTax, BigDecimal quantity, String orderNumber, ServiceInstance serviceInstance,
            OneShotChargeTemplate chargeTemplate) {
        super(description, chargeDate, amountWithoutTax, amountWithTax, quantity, orderNumber, serviceInstance, chargeTemplate);
    }
    
    /**
     * Constructor
     * 
     * @param description Charge description (to override a value from a charge template). Optional
     * @param chargeDate Charge date
     * @param amountWithoutTax Amount without tax
     * @param amountWithTax Amount with tax
     * @param quantity Quantity
     * @param orderNumber Order number
     * @param subscription Subscription
     * @param chargeTemplate Charge template
     */
    public TerminationChargeInstance(String description, Date chargeDate, BigDecimal amountWithoutTax, BigDecimal amountWithTax, BigDecimal quantity, String orderNumber, Subscription subscription,
            OneShotChargeTemplate chargeTemplate) {
        super(description,  chargeDate,  amountWithoutTax,  amountWithTax,  quantity,  orderNumber,  subscription,
             chargeTemplate);
    }
}