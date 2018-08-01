package org.meveo.api.dto.catalog;

import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CustomFieldsDto;
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

    /** The filter expression. */
    @Size(max = 2000)
    private String filterExpression = null;
    
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
        setFilterExpression(recurringChargeTemplate.getFilterExpression());
        if (recurringChargeTemplate.getShareLevel() != null) {
            shareLevel = recurringChargeTemplate.getShareLevel().getId();
        }
        if (recurringChargeTemplate.getCalendar() != null) {
            calendar = recurringChargeTemplate.getCalendar().getCode();
        }
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

    /* (non-Javadoc)
     * @see org.meveo.api.dto.catalog.ChargeTemplateDto#toString()
     */
    @Override
    public String toString() {
        return "RecurringChargeTemplateDto [calendar=" + calendar + ", durationTermInMonth=" + durationTermInMonth + ", subscriptionProrata=" + subscriptionProrata
                + ", terminationProrata=" + terminationProrata + ", applyInAdvance=" + applyInAdvance + ", shareLevel=" + shareLevel + ", filterExpression=" + filterExpression
                + ", calendarCodeEl=" + calendarCodeEl + ", durationTermInMonthEl=" + durationTermInMonthEl + ", subscriptionProrataEl=" + subscriptionProrataEl
                + ", terminationProrataEl=" + terminationProrataEl + ", applyInAdvanceEl=" + applyInAdvanceEl + "]";
    }
}