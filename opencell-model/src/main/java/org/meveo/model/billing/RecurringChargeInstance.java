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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.meveo.model.admin.Seller;
import org.meveo.model.catalog.RecurringChargeTemplate;

/**
 * One shot charge as part of subscribed service
 * 
 * @author Andrius Karpavicius
 * @author Abdellatif BARI
 * @lastModifiedVersion 5.3
 */

@Entity
@DiscriminatorValue("R")
public class RecurringChargeInstance extends ChargeInstance {

    private static final long serialVersionUID = 1L;

    /**
     * Charge template/definition that charge was instantiated from
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charge_template_id", insertable = false, updatable = false)
    private RecurringChargeTemplate recurringChargeTemplate;

    /**
     * Subscription date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "subscription_date")
    protected Date subscriptionDate;

    /**
     * Next charge date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "next_charge_date")
    protected Date nextChargeDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counter_id")
    private CounterInstance counter;

    /**
     * Quantity subscribed
     */
    @Column(name = "quantity", precision = NB_PRECISION, scale = NB_DECIMALS, nullable = false)
    @NotNull
    protected BigDecimal quantity = BigDecimal.ONE;

    public RecurringChargeInstance() {

    }

    public RecurringChargeInstance(BigDecimal amountWithoutTax, BigDecimal amountWithTax, RecurringChargeTemplate recurringChargeTemplate, ServiceInstance serviceInstance,
            InstanceStatusEnum status) {

        super(amountWithoutTax, amountWithTax, recurringChargeTemplate, serviceInstance, status);

        this.recurringChargeTemplate = recurringChargeTemplate;
        this.quantity = serviceInstance.getQuantity() == null ? BigDecimal.ONE : serviceInstance.getQuantity();

    }

    public RecurringChargeInstance(BigDecimal amountWithoutTax, BigDecimal amountWithTax, BigDecimal quantity, Date subscriptionDate, Subscription subscription, Seller seller,
            TradingCountry tradingCountry, TradingCurrency tradingCurrency, RecurringChargeTemplate recurringChargeTemplate) {

        this.code = recurringChargeTemplate.getCode();
        this.description = recurringChargeTemplate.getDescription();

        this.subscriptionDate = subscriptionDate;
        this.chargeDate = subscriptionDate;
        this.amountWithoutTax = amountWithoutTax;
        this.amountWithTax = amountWithTax;
        this.chargeTemplate = recurringChargeTemplate;
        this.recurringChargeTemplate = recurringChargeTemplate;
        this.setQuantity(quantity == null ? BigDecimal.ONE : quantity);
        this.setSubscription(subscription);
        this.country = tradingCountry;
        this.currency = tradingCurrency;
        if (subscription != null) {
            this.seller = subscription.getSeller();
        }
    }

    public RecurringChargeTemplate getRecurringChargeTemplate() {
        return recurringChargeTemplate;
    }

    public void setRecurringChargeTemplate(RecurringChargeTemplate recurringChargeTemplate) {
        this.recurringChargeTemplate = recurringChargeTemplate;
        this.code = recurringChargeTemplate.getCode();
        this.description = recurringChargeTemplate.getDescription();
    }

    public Date getSubscriptionDate() {
        return subscriptionDate;
    }

    public void setSubscriptionDate(Date subscriptionDate) {
        this.subscriptionDate = subscriptionDate;
    }

    public Date getNextChargeDate() {
        return nextChargeDate;
    }

    public void setNextChargeDate(Date nextChargeDate) {
        this.nextChargeDate = nextChargeDate;
    }

    /**
     * @return Quantity subscribed
     */
    public BigDecimal getQuantity() {
        return quantity;
    }

    /**
     * @param quantity Quantity subscribed
     */
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public CounterInstance getCounter() {
        return counter;
    }

    public void setCounter(CounterInstance counter) {
        this.counter = counter;
    }
}