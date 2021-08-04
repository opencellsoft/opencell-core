/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

/**
 * Usage charge as part of subscribed service
 * 
 * @author Andrius Karpavicius
 */
@Entity
@DiscriminatorValue("U")
@NamedQueries({
        @NamedQuery(name = "UsageChargeInstance.getActiveUsageChargesBySubscriptionId", query = "SELECT c FROM UsageChargeInstance c where c.status='ACTIVE' and c.subscription.id=:subscriptionId order by c.priority ASC", hints = {
                @QueryHint(name = "org.hibernate.cacheable", value = "true") }),
        @NamedQuery(name = "UsageChargeInstance.getActiveUsageCharges", query = "SELECT c FROM UsageChargeInstance c where c.status='ACTIVE'  order by c.priority ASC", hints = {
                @QueryHint(name = "org.hibernate.cacheable", value = "true") }),
        @NamedQuery(name = "UsageChargeInstance.getUsageChargesValidesForDateBySubscription", query = "SELECT c FROM UsageChargeInstance c where (c.status='ACTIVE' OR ((c.status='TERMINATED' OR c.status='SUSPENDED') AND c.terminationDate>:terminationDate)) and c.subscription.id=:subscriptionId order by c.priority ASC", hints = {
                })})
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
    @Column(name = "priority", columnDefinition = "int default 1")
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

        this.ratingUnitDescription = usageChargeTemplate.getRatingUnitDescription();
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
}