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
package org.meveo.model.catalog;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;

/**
 * The Class RecurringChargeTemplate.
 *
 * @author anasseh
 * @lastModifiedVersion 5.0.2
 */
@Entity
@Table(name = "cat_recurring_charge_templ")
@NamedQueries({			
@NamedQuery(name = "recurringChargeTemplate.getNbrRecurringChrgWithNotPricePlan", 
	           query = "select count (*) from RecurringChargeTemplate r where r.code not in (select p.eventCode from  PricePlanMatrix p where p.eventCode is not null) "),
	           
@NamedQuery(name = "recurringChargeTemplate.getRecurringChrgWithNotPricePlan", 
	           query = "from RecurringChargeTemplate r where r.code not in (select p.eventCode from  PricePlanMatrix p where p.eventCode is not null) "),
	           
@NamedQuery(name = "recurringChargeTemplate.getNbrRecurringChrgNotAssociated", 
	           query = "select count(*) from RecurringChargeTemplate r where (r.id not in (select serv.chargeTemplate from ServiceChargeTemplateRecurring serv) "
	           		+ " OR r.code not in (select p.eventCode from  PricePlanMatrix p where p.eventCode is not null))   "),
	           		
@NamedQuery(name = "recurringChargeTemplate.getRecurringChrgNotAssociated", 
	 	           query = "from RecurringChargeTemplate r where (r.id not in (select serv.chargeTemplate from ServiceChargeTemplateRecurring serv) "
	 	           		+ " OR r.code not in (select p.eventCode from  PricePlanMatrix p where p.eventCode is not null))  ")	                
	       })
public class RecurringChargeTemplate extends ChargeTemplate {
	
	/** The Constant CHARGE_TYPE. */
	@Transient
	public static final String CHARGE_TYPE = "RECURRING";

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The recurrence type. */
	@Enumerated(EnumType.STRING)
	@Column(name = "recurrence_type")
	private RecurrenceTypeEnum recurrenceType = RecurrenceTypeEnum.CALENDAR;

	/** The calendar. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "calendar_id")
	private Calendar calendar;

	/** The duration term in month. */
	@Column(name = "duration_term_in_month")
	private Integer durationTermInMonth;

	/** The subscription prorata. */
	@Type(type="numeric_boolean")
    @Column(name = "subscription_prorata")
	private Boolean subscriptionProrata;

	/** The termination prorata. */
	@Type(type="numeric_boolean")
    @Column(name = "termination_prorata")
	private Boolean terminationProrata;

	/** The apply in advance. */
	@Type(type="numeric_boolean")
    @Column(name = "apply_in_advance")
	private Boolean applyInAdvance;

	/** The share level. */
	@Enumerated(EnumType.STRING)
	@Column(name= "share_level",length=20)
	private LevelEnum shareLevel;
	
	/** The filter expression. */
	@Column(name = "filter_expression", length = 2000)
	@Size(max = 2000)
	private String filterExpression = null;
	
    /** The subscription prorata EL. */
    @Column(name = "subscription_prorata_el", length = 2000)
    @Size(max = 2000)
    private String subscriptionProrataEl = null;
    
    /** The termination prorata EL. */
    @Column(name = "termination_prorata_el", length = 2000)
    @Size(max = 2000)
    private String terminationProrataEl;
	
    /** The apply in advance EL. */
    @Column(name = "apply_in_advance_el", length = 2000)
    @Size(max = 2000)
    private String applyInAdvanceEl;
    
    /** The duration term in month EL. */
    @Column(name = "duration_term_in_month_el", length = 2000)
    @Size(max = 2000)
    private String durationTermInMonthEl;
    
    /** The calendar code EL. */
    @Column(name = "calendar_code_el", length = 2000)
    @Size(max = 2000)
    private String calendarCodeEl;
    
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

	/**
     * Gets the filter expression.
     *
     * @return the filter expression
     */
	public String getFilterExpression() {
		return filterExpression;
	}

	/**
     * Sets the filter expression.
     *
     * @param filterExpression the new filter expression
     */
	public void setFilterExpression(String filterExpression) {
		this.filterExpression = filterExpression;
	}

	/* (non-Javadoc)
	 * @see org.meveo.model.catalog.ChargeTemplate#getChargeType()
	 */
	public String getChargeType() {
		return CHARGE_TYPE;
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
    
    
}
