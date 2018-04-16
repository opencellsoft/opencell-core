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
 * @lastModifiedVersion 5.0
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

    /**
     * Instantiates a new recurring charge template dto.
     */
    public RecurringChargeTemplateDto() {

    }

    /**
     * Instantiates a new recurring charge template dto.
     *
     * @param e the e
     * @param customFieldInstances the custom field instances
     */
    public RecurringChargeTemplateDto(RecurringChargeTemplate e, CustomFieldsDto customFieldInstances) {
        super(e, customFieldInstances);
        durationTermInMonth = e.getDurationTermInMonth();
        subscriptionProrata = e.getSubscriptionProrata();
        terminationProrata = e.getTerminationProrata();
        applyInAdvance = e.getApplyInAdvance();
        setFilterExpression(e.getFilterExpression());
        if (e.getShareLevel() != null) {
            shareLevel = e.getShareLevel().getId();
        }
        if (e.getCalendar() != null) {
            calendar = e.getCalendar().getCode();
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

    @Override
    public String toString() {
        return "RecurringChargeTemplateDto [calendar=" + calendar + ", durationTermInMonth=" + durationTermInMonth + ", subscriptionProrata=" + subscriptionProrata
                + ", terminationProrata=" + terminationProrata + ", applyInAdvance=" + applyInAdvance + ", shareLevel=" + shareLevel + ", getCode()=" + getCode()
                + ", getDescription()=" + getDescription() + ", getLanguageDescriptions()=" + getLanguageDescriptions() + ", toString()=" + super.toString()
                + ", getAmountEditable()=" + getAmountEditable() + ", getInvoiceSubCategory()=" + getInvoiceSubCategory() + ", isDisabled()=" + isDisabled() + ", getClass()="
                + getClass() + ", hashCode()=" + hashCode() + "]";
    }
}