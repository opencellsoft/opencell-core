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

import org.hibernate.annotations.Type;
import org.meveo.model.admin.Seller;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.RecurringChargeTemplate;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * One shot charge as part of subscribed service
 * 
 * @author Andrius Karpavicius
 * @author Abdellatif BARI
 * @lastModifiedVersion 5.3
 */

@Entity
@DiscriminatorValue("R")
@NamedQueries({ @NamedQuery(name = "RecurringChargeInstance.listToRateByStatusAndDate", query = "SELECT c.id FROM RecurringChargeInstance c where c.status=:status and (c.nextChargeDate is null OR c.nextChargeDate<:maxNextChargeDate)"),
        @NamedQuery(name = "RecurringChargeInstance.listToRateByStatusBCAndDate", query = "SELECT c.id FROM RecurringChargeInstance c where c.status=:status and (c.nextChargeDate is null OR c.nextChargeDate<:maxNextChargeDate) and c.userAccount.billingAccount.billingCycle in :billingCycles") })
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

    /**
     * The calendar
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id")
    private Calendar calendar;

    /**
     * The calendar init date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "calendar_init_date")
    private Date calendarInitDate;

    /**
     * Apply charge in advance - at the beginning of the period. If false, charge will be applied at the end of the period
     */
    @Type(type = "numeric_boolean")
    @Column(name = "apply_in_advance")
    private Boolean applyInAdvance;

    public RecurringChargeInstance() {

    }

    public RecurringChargeInstance(BigDecimal amountWithoutTax, BigDecimal amountWithTax, RecurringChargeTemplate recurringChargeTemplate, ServiceInstance serviceInstance,
            InstanceStatusEnum status) {

        super(amountWithoutTax, amountWithTax, recurringChargeTemplate, serviceInstance, status);

        this.recurringChargeTemplate = recurringChargeTemplate;
        this.quantity = serviceInstance.getQuantity() == null ? BigDecimal.ONE : serviceInstance.getQuantity();

    }
    public RecurringChargeInstance(BigDecimal amountWithoutTax, BigDecimal amountWithTax, RecurringChargeTemplate recurringChargeTemplate,
                                   ServiceInstance serviceInstance, InstanceStatusEnum status, Calendar calendar, Date calendarInitDate, Boolean applyInAdvance) {
        this(amountWithoutTax, amountWithTax, recurringChargeTemplate, serviceInstance, status);
        this.calendar = calendar;
        this.calendarInitDate = calendarInitDate;
        this.applyInAdvance = applyInAdvance;
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

    /**
     * Gets the calendar.
     *
     * @return the calendar
     */
    public Calendar getCalendar() {
        return calendar;
    }

    /**
     * Sets the calendar.
     *
     * @param calendar the new calendar
     */
    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    /**
     * Gets the calendar initial date
     *
     * @return the calendar initial date
     */
    public Date getCalendarInitDate() {
        return calendarInitDate;
    }

    /**
     * Sets the calendar initial date
     *
     * @param calendarInitDate the new calendar initial date
     */
    public void setCalendarInitDate(Date calendarInitDate) {
        this.calendarInitDate = calendarInitDate;
    }

    /**
     * Gets the apply in advance.
     *
     * @return the apply in advance
     */
    public Boolean getApplyInAdvance() {
        return applyInAdvance;
    }

    /**
     * Sets the apply in advance.
     *
     * @param applyInAdvance the new apply in advance
     */
    public void setApplyInAdvance(Boolean applyInAdvance) {
        this.applyInAdvance = applyInAdvance;
    }

    @Override
    public ChargeTemplate.ChargeMainTypeEnum getChargeMainType() {
        return ChargeTemplate.ChargeMainTypeEnum.RECURRING;
    }
}