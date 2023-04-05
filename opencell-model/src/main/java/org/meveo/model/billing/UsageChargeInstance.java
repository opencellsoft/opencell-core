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

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.validation.constraints.Size;

import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.model.catalog.ChargeTemplate.ChargeMainTypeEnum;

/**
 * Usage charge as part of subscribed service
 * 
 * @author Andrius Karpavicius
 * @author Khalid HORRI
 * @lastModifiedVersion 9.0
 */
@Entity
@DiscriminatorValue("U")
@NamedQueries({
        @NamedQuery(name = "UsageChargeInstance.getActiveUsageChargesBySubscriptionId", query = "SELECT c FROM UsageChargeInstance c where c.status='ACTIVE' and c.subscription.id=:subscriptionId order by c.priority ASC", hints = {
                @QueryHint(name = "org.hibernate.cacheable", value = "true") }),
        @NamedQuery(name = "UsageChargeInstance.getActiveUsageCharges", query = "SELECT c FROM UsageChargeInstance c where c.status='ACTIVE'  order by c.priority ASC", hints = {
                @QueryHint(name = "org.hibernate.cacheable", value = "true") }),
        @NamedQuery(name = "UsageChargeInstance.getUsageChargesValidesForDateBySubscription",
                query = "SELECT c FROM UsageChargeInstance c left join fetch c.chargeTemplate where (c.status='ACTIVE' OR ((c.status='TERMINATED' OR c.status='SUSPENDED') AND c.terminationDate>:terminationDate)) and c.subscription.id=:subscriptionId order by c.priority ASC")})
public class UsageChargeInstance extends ChargeInstance {

    private static final long serialVersionUID = 1L;

    /**
     * Counter for consumption tracking
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counter_id")
    private CounterInstance counter;

    /**
     * Rating unit description
     */
    @Column(name = "rating_unit_description", length = 20)
    @Size(max = 20)
    private String ratingUnitDescription;

    /**
     * The lower number, the higher the priority is. Value is a copy from UsageChargeTemplate.priority field and is synchronized when UsageChargeTemplate.priority value change.
     */
    @Column(name = "priority")
    private int priority = 1;

    /**
     * Instantiate Usage charge instance
     */
    public UsageChargeInstance() {
    }

    /**
     * Instantiate Usage charge instance from a Usage charge template
     * 
     * @param amountWithoutTax Amount without tax to override
     * @param amountWithTax Amount with tax to override
     * @param usageChargeTemplate Usage charge template to instantiate from
     * @param serviceInstance Service instance that charge will belong to
     * @param status Activation status
     */
    public UsageChargeInstance(BigDecimal amountWithoutTax, BigDecimal amountWithTax, UsageChargeTemplate usageChargeTemplate, ServiceInstance serviceInstance,
            InstanceStatusEnum status) {

        super(amountWithoutTax, amountWithTax, usageChargeTemplate, serviceInstance, status);

        String chargeRatingUnitDescription = usageChargeTemplate.getRatingUnitDescription();
		this.ratingUnitDescription = chargeRatingUnitDescription==null || chargeRatingUnitDescription.length()<20?chargeRatingUnitDescription:chargeRatingUnitDescription.substring(0,20);
        this.priority = usageChargeTemplate.getPriority();
    }

    public CounterInstance getCounter() {
        return counter;
    }

    public void setCounter(CounterInstance counter) {
        this.counter = counter;
    }

    public String getRatingUnitDescription() {
        return ratingUnitDescription;
    }

    public void setRatingUnitDescription(String ratingUnitDescription) {
        this.ratingUnitDescription = ratingUnitDescription;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public ChargeMainTypeEnum getChargeMainType() {
        return ChargeMainTypeEnum.USAGE;
    }
}