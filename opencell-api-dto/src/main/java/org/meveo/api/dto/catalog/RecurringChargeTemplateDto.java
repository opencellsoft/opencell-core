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

package org.meveo.api.dto.catalog;

import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.catalog.RecurrenceTypeEnum;
import org.meveo.model.catalog.RecurringChargeTemplate;

/**
 * The Class RecurringChargeTemplateDto.
 *
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0.2
 */
@XmlRootElement(name = "RecurringChargeTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class RecurringChargeTemplateDto extends ChargeTemplateDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1652193975405244532L;

    /** The calendar. */
    @XmlElement(required = true)
    private String calendar;

    /** The duration term in month. */
    private Integer durationTermInMonth;

    /** The subscription prorata. */
    private Boolean subscriptionProrata;

    /** The termination prorata. */
    private Boolean terminationProrata;

    /** The apply in advance. */
    private Boolean applyInAdvance = false;

    /** The share level. */
    private Integer shareLevel;

    /** The calendar code el. */
    @Size(max = 2000)
    private String calendarCodeEl;

    /** The duration term in month El. */
    @Size(max = 2000)
    private String durationTermInMonthEl;

    /** The subscription prorata el. */
    @Size(max = 2000)
    private String subscriptionProrataEl;

    /** The termination prorata el. */
    @Size(max = 2000)
    private String terminationProrataEl;

    /** The apply in advance el. */
    @Size(max = 2000)
    private String applyInAdvanceEl;

    /**
     * Expression to determine and override the date that recurring charge should be charged to upon charge/service termination
     */
    private String applyTerminatedChargeToDateEL;
    
    
    private RecurrenceTypeEnum recurrenceType = RecurrenceTypeEnum.CALENDAR;

    private String attributeDurationCode;
    
    private String attributeCalendarCode;
    /**
     * Instantiates a new recurring charge template dto.
     */
    public RecurringChargeTemplateDto() {

    }

    /**
     * Instantiates a new recurring charge template dto.
     *
     * @param recurringChargeTemplate the RecurringChargeTemplate entity
     * @param customFieldInstances the custom field instances
     */
    public RecurringChargeTemplateDto(RecurringChargeTemplate recurringChargeTemplate, CustomFieldsDto customFieldInstances) {
        super(recurringChargeTemplate, customFieldInstances);
        durationTermInMonth = recurringChargeTemplate.getDurationTermInMonth();
        subscriptionProrata = recurringChargeTemplate.getSubscriptionProrata();
        terminationProrata = recurringChargeTemplate.getTerminationProrata();
        applyInAdvance = recurringChargeTemplate.getApplyInAdvance();
        durationTermInMonthEl = recurringChargeTemplate.getDurationTermInMonthEl();
        subscriptionProrataEl = recurringChargeTemplate.getSubscriptionProrataEl();
        terminationProrataEl = recurringChargeTemplate.getTerminationProrataEl();
        applyInAdvanceEl = recurringChargeTemplate.getApplyInAdvanceEl();
        calendarCodeEl = recurringChargeTemplate.getCalendarCodeEl();
        applyTerminatedChargeToDateEL = recurringChargeTemplate.getApplyTerminatedChargeToDateEL();
        if (recurringChargeTemplate.getShareLevel() != null) {
            shareLevel = recurringChargeTemplate.getShareLevel().getId();
        }
        if (recurringChargeTemplate.getCalendar() != null) {
            calendar = recurringChargeTemplate.getCalendar().getCode();
        }
        recurrenceType=recurringChargeTemplate.getRecurrenceType();
        this.attributeCalendarCode = recurringChargeTemplate.getAttributeCalendar() != null ? recurringChargeTemplate.getAttributeCalendar().getCode() : null;
        this.attributeDurationCode = recurringChargeTemplate.getAttributeDuration() != null ? recurringChargeTemplate.getAttributeDuration().getCode() : null;
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
        return subscriptionProrata == null ? false : subscriptionProrata;
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
        return terminationProrata == null ? false : terminationProrata;
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
    public Integer getShareLevel() {
        return shareLevel;
    }

    /**
     * Sets the share level.
     *
     * @param shareLevel the new share level
     */
    public void setShareLevel(Integer shareLevel) {
        this.shareLevel = shareLevel;
    }

    /**
     * Gets the calendar.
     *
     * @return the calendar
     */
    public String getCalendar() {
        return calendar;
    }

    /**
     * Sets the calendar.
     *
     * @param calendar the new calendar
     */
    public void setCalendar(String calendar) {
        this.calendar = calendar;
    }

    /**
     * Gets the calendar code el.
     *
     * @return the calendarCodeEl
     */
    public String getCalendarCodeEl() {
        return calendarCodeEl;
    }

    /**
     * Sets the calendar code el.
     *
     * @param calendarCodeEl the calendarCodeEl to set
     */
    public void setCalendarCodeEl(String calendarCodeEl) {
        this.calendarCodeEl = calendarCodeEl;
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
     * Gets the subscription prorata el.
     *
     * @return the subscriptionProrataEl
     */
    public String getSubscriptionProrataEl() {
        return subscriptionProrataEl;
    }

    /**
     * Sets the subscription prorata el.
     *
     * @param subscriptionProrataEl the subscriptionProrataEl to set
     */
    public void setSubscriptionProrataEl(String subscriptionProrataEl) {
        this.subscriptionProrataEl = subscriptionProrataEl;
    }

    /**
     * Gets the termination prorata el.
     *
     * @return the terminationProrataEl
     */
    public String getTerminationProrataEl() {
        return terminationProrataEl;
    }

    /**
     * Sets the termination prorata el.
     *
     * @param terminationProrataEl the terminationProrataEl to set
     */
    public void setTerminationProrataEl(String terminationProrataEl) {
        this.terminationProrataEl = terminationProrataEl;
    }

    /**
     * Gets the apply in advance el.
     *
     * @return the applyInAdvanceEl
     */
    public String getApplyInAdvanceEl() {
        return applyInAdvanceEl;
    }

    /**
     * Sets the apply in advance el.
     *
     * @param applyInAdvanceEl the applyInAdvanceEl to set
     */
    public void setApplyInAdvanceEl(String applyInAdvanceEl) {
        this.applyInAdvanceEl = applyInAdvanceEl;
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
	 * @return the recurrenceType
	 */
	public RecurrenceTypeEnum getRecurrenceType() {
		return recurrenceType;
	}

	/**
	 * @param recurrenceType the recurrenceType to set
	 */
	public void setRecurrenceType(RecurrenceTypeEnum recurrenceType) {
		this.recurrenceType = recurrenceType;
	}

	/*
     * (non-Javadoc)
     * 
     * @see org.meveo.api.dto.catalog.ChargeTemplateDto#toString()
     */
    @Override
    public String toString() {
        return "RecurringChargeTemplateDto [" + super.toString() + ", calendar=" + calendar + ", durationTermInMonth=" + durationTermInMonth + ", subscriptionProrata=" + subscriptionProrata + ", terminationProrata="
                + terminationProrata + ", applyInAdvance=" + applyInAdvance + ", shareLevel=" + shareLevel + ", calendarCodeEl=" + calendarCodeEl + ", durationTermInMonthEl=" + durationTermInMonthEl
                + ", subscriptionProrataEl=" + subscriptionProrataEl + ", terminationProrataEl=" + terminationProrataEl + ", applyInAdvanceEl=" + applyInAdvanceEl + "]";
    }

	/**
	 * @return the attributeDurationCode
	 */
	public String getAttributeDurationCode() {
		return attributeDurationCode;
	}

	/**
	 * @param attributeDurationCode the attributeDurationCode to set
	 */
	public void setAttributeDurationCode(String attributeDurationCode) {
		this.attributeDurationCode = attributeDurationCode;
	}

	/**
	 * @return the attributeCalendarCode
	 */
	public String getAttributeCalendarCode() {
		return attributeCalendarCode;
	}

	/**
	 * @param attributeCalendarCode the attributeCalendarCode to set
	 */
	public void setAttributeCalendarCode(String attributeCalendarCode) {
		this.attributeCalendarCode = attributeCalendarCode;
	}
}