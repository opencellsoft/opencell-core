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
package org.meveo.model.catalog;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.meveo.model.cpq.Attribute;

/**
 * Recurring charge template
 *
 * @author anasseh
 * @lastModifiedVersion 5.0.2
 */
@Entity
@DiscriminatorValue("R")
@NamedQueries({
        @NamedQuery(name = "recurringChargeTemplate.getNbrRecurringChrgNotAssociated", query = "select count(*) from RecurringChargeTemplate r where (r.id not in (select distinct serv.chargeTemplate.id from ServiceChargeTemplateRecurring serv) "
                + " OR r.code not in (select distinct p.eventCode from  PricePlanMatrix p where p.eventCode is not null))", hints = { @QueryHint(name = "org.hibernate.cacheable", value = "TRUE") }),

        @NamedQuery(name = "recurringChargeTemplate.getRecurringChrgNotAssociated", query = "from RecurringChargeTemplate r where (r.id not in (select distinct serv.chargeTemplate.id from ServiceChargeTemplateRecurring serv) "
                + " OR r.code not in (select distinct p.eventCode from  PricePlanMatrix p where p.eventCode is not null))  ") })
public class RecurringChargeTemplate extends ChargeTemplate {

    private static final long serialVersionUID = -7456322224120515575L;

    /**
     * The recurrence type
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "recurrence_type")
    private RecurrenceTypeEnum recurrenceType = RecurrenceTypeEnum.CALENDAR;

    /**
     * The calendar
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id")
    private Calendar calendar;

    /**
     * The duration term in month
     */
    @Column(name = "duration_term_in_month")
    private Integer durationTermInMonth;

    /**
     * Prorate amount when subscribing
     */
    @Type(type = "numeric_boolean")
    @Column(name = "subscription_prorata")
    private Boolean subscriptionProrata;

    /**
     * Prorate amount when terminating
     */
    @Type(type = "numeric_boolean")
    @Column(name = "termination_prorata")
    private Boolean terminationProrata;

    /**
     * Prorata On Price Change subscribed
     */
    @Type(type = "numeric_boolean")
    @Column(name = "prorata_on_price_change", nullable = false)
    @NotNull
    private boolean prorataOnPriceChange = false;

    /**
     * Apply charge in advance - at the beginning of the period. If false, charge will be applied at the end of the period
     */
    @Type(type = "numeric_boolean")
    @Column(name = "apply_in_advance")
    private Boolean applyInAdvance;

    /**
     * At what account level charge is shared
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "share_level", length = 20)
    private LevelEnum shareLevel;

    /**
     * Expression to determine if subscription amount is prorated
     */
    @Column(name = "subscription_prorata_el", length = 2000)
    @Size(max = 2000)
    private String subscriptionProrataEl = null;

    /**
     * Expression to determine if termination amount is prorated
     */
    @Column(name = "termination_prorata_el", length = 2000)
    @Size(max = 2000)
    private String terminationProrataEl;

    /**
     * Expression to determine if charge should be applied in advance - at the start of recurring period
     */
    @Column(name = "apply_in_advance_el", length = 2000)
    @Size(max = 2000)
    private String applyInAdvanceEl;

    /**
     * Expression to determine duration term in month
     */
    @Column(name = "duration_term_in_month_el", length = 2000)
    @Size(max = 2000)
    private String durationTermInMonthEl;

    /**
     * Expression to determine calendar code
     */
    @Column(name = "calendar_code_el", length = 2000)
    @Size(max = 2000)
    private String calendarCodeEl;

    /**
     * Expression to determine and override the date that recurring charge should be charged to upon charge/service termination
     */
    @Column(name = "apply_terminated_charge_to_date_el", length = 2000)
    @Size(max = 2000)
    private String applyTerminatedChargeToDateEL;
    

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_duration_id")
    private Attribute attributeDuration;
    

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_calendar_id")
    private Attribute attributeCalendar;

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
     * Gets the recurrence type.
     *
     * @return the recurrence type
     */
    public RecurrenceTypeEnum getRecurrenceType() {
        return recurrenceType;
    }

    /**
     * Sets the recurrence type.
     *
     * @param recurrenceType the new recurrence type
     */
    public void setRecurrenceType(RecurrenceTypeEnum recurrenceType) {
        this.recurrenceType = recurrenceType;
    }

    /**
     * Gets the duration term in month.
     *
     * @return the duration term in month
     */
    public Integer getDurationTermInMonth() {
        return durationTermInMonth;
    }

    /**
     * Sets the duration term in month.
     *
     * @param durationTermInMonth the new duration term in month
     */
    public void setDurationTermInMonth(Integer durationTermInMonth) {
        this.durationTermInMonth = durationTermInMonth;
    }

    /**
     * Gets the subscription prorata.
     *
     * @return the subscription prorata
     */
    public Boolean getSubscriptionProrata() {
        return subscriptionProrata;
    }

    /**
     * Sets the subscription prorata.
     *
     * @param subscriptionProrata the new subscription prorata
     */
    public void setSubscriptionProrata(Boolean subscriptionProrata) {
        this.subscriptionProrata = subscriptionProrata;
    }

    /**
     * Gets the termination prorata.
     *
     * @return the termination prorata
     */
    public Boolean getTerminationProrata() {
        return terminationProrata;
    }

    /**
     * Sets the termination prorata.
     *
     * @param terminationProrata the new termination prorata
     */
    public void setTerminationProrata(Boolean terminationProrata) {
        this.terminationProrata = terminationProrata;
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

    /**
     * Gets the share level.
     *
     * @return the share level
     */
    public LevelEnum getShareLevel() {
        return shareLevel;
    }

    /**
     * Sets the share level.
     *
     * @param shareLevel the new share level
     */
    public void setShareLevel(LevelEnum shareLevel) {
        this.shareLevel = shareLevel;
    }

    @Override
    public ChargeMainTypeEnum getChargeMainType() {
        return ChargeMainTypeEnum.RECURRING;
    }

    /**
     * Gets the subscription prorata El.
     *
     * @return the subscriptionProrataEL
     */
    public String getSubscriptionProrataEl() {
        return subscriptionProrataEl;
    }

    /**
     * Sets the subscription prorata El.
     *
     * @param subscriptionProrataEl the subscriptionProrataEL to set
     */
    public void setSubscriptionProrataEl(String subscriptionProrataEl) {
        this.subscriptionProrataEl = subscriptionProrataEl;
    }

    /**
     * @return the terminationProrataEl
     */
    public String getTerminationProrataEl() {
        return terminationProrataEl;
    }

    /**
     * @param terminationProrataEl the terminationProrataEl to set
     */
    public void setTerminationProrataEl(String terminationProrataEl) {
        this.terminationProrataEl = terminationProrataEl;
    }

    /**
     * @return the applyInAdvanceEl
     */
    public String getApplyInAdvanceEl() {
        return applyInAdvanceEl;
    }

    /**
     * @param applyInAdvanceEl the applyInAdvanceEl to set
     */
    public void setApplyInAdvanceEl(String applyInAdvanceEl) {
        this.applyInAdvanceEl = applyInAdvanceEl;
    }

    /**
     * @return the durationTermInMonthEl
     */
    public String getDurationTermInMonthEl() {
        return durationTermInMonthEl;
    }

    /**
     * @param durationTermInMonthEl the durationTermInMonthEl to set
     */
    public void setDurationTermInMonthEl(String durationTermInMonthEl) {
        this.durationTermInMonthEl = durationTermInMonthEl;
    }

    /**
     * @return the calendarCodeEl
     */
    public String getCalendarCodeEl() {
        return calendarCodeEl;
    }

    /**
     * @param calendarCodeEl the calendarCodeEl to set
     */
    public void setCalendarCodeEl(String calendarCodeEl) {
        this.calendarCodeEl = calendarCodeEl;
    }

    public boolean isProrataOnPriceChange() {
        return prorataOnPriceChange;
    }

    public void setProrataOnPriceChange(boolean prorataOnPriceChange) {
        this.prorataOnPriceChange = prorataOnPriceChange;
    }
    
    /**
     * @return Expression to determine and override the date that recurring charge should be charged to upon charge/service termination
     */
    public String getApplyTerminatedChargeToDateEL() {
        return applyTerminatedChargeToDateEL;
    }
    
    /**
     * @param applyTerminatedChargeToDateEL Expression to determine and override the date that recurring charge should be charged to upon charge/service termination
     */
    public void setApplyTerminatedChargeToDateEL(String applyTerminatedChargeToDateEL) {
        this.applyTerminatedChargeToDateEL = applyTerminatedChargeToDateEL;
    }

	/**
	 * @return the attributeDuration
	 */
	public Attribute getAttributeDuration() {
		return attributeDuration;
	}

	/**
	 * @param attributeDuration the attributeDuration to set
	 */
	public void setAttributeDuration(Attribute attributeDuration) {
		this.attributeDuration = attributeDuration;
	}

	/**
	 * @return the attributeCalendar
	 */
	public Attribute getAttributeCalendar() {
		return attributeCalendar;
	}

	/**
	 * @param attributeCalendar the attributeCalendar to set
	 */
	public void setAttributeCalendar(Attribute attributeCalendar) {
		this.attributeCalendar = attributeCalendar;
	}
}