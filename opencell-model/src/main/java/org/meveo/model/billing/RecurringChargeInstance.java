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
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.admin.Seller;
import org.meveo.model.catalog.RecurringChargeTemplate;

/**
 * One shot charge as part of subscribed service
 * 
 * @author Andrius Karpavicius
 */
@Entity
@Table(name = "billing_recurring_charge_inst")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "billing_recurring_chrg_inst_seq"), })
public class RecurringChargeInstance extends ChargeInstance {

    private static final long serialVersionUID = 1L;

    /**
     * Charge template/definition that charge was instantiated from
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recurring_chrg_tmpl_id")
    private RecurringChargeTemplate recurringChargeTemplate;

    /**
     * Subscribed service
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_instance_id")
    protected ServiceInstance serviceInstance;

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
        this.serviceInstance = serviceInstance;
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
        this.setSeller(subscription.getSeller());
        this.country = tradingCountry;
        this.currency = tradingCurrency;
    }

    public RecurringChargeTemplate getRecurringChargeTemplate() {
        return recurringChargeTemplate;
    }

    public void setRecurringChargeTemplate(RecurringChargeTemplate recurringChargeTemplate) {
        this.recurringChargeTemplate = recurringChargeTemplate;
        this.code = recurringChargeTemplate.getCode();
        this.description = recurringChargeTemplate.getDescription();
    }

    public ServiceInstance getServiceInstance() {
        return serviceInstance;
    }

    public void setServiceInstance(ServiceInstance serviceInstance) {
        this.serviceInstance = serviceInstance;
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

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
}