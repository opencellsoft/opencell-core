package org.meveo.model.crm;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.meveo.model.BaseProviderlessEntity;
import org.meveo.model.ExportIdentifier;

@Entity
@ExportIdentifier({ "customFieldInstance.code", "customFieldInstance.provider", "customFieldInstance.subscription.code", "customFieldInstance.account.code",
        "customFieldInstance.chargeTemplate.code", "customFieldInstance.serviceTemplate.code", "customFieldInstance.offerTemplate.code", "customFieldInstance.access.accessUserId",
        "customFieldInstance.access.subscription.code", "customFieldInstance.jobInstance.code", "periodStartDate", "periodEndDate" })
@Table(name = "CRM_CUSTOM_FIELD_PERIOD", uniqueConstraints = @UniqueConstraint(columnNames = { "CF_INSTANCE_ID", "PERIOD_START_DATE", "PERIOD_END_DATE" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CRM_CUSTOM_FIELD_PERIOD_SEQ")
public class CustomFieldPeriod extends BaseProviderlessEntity {

    private static final long serialVersionUID = -3613016075735338913L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CF_INSTANCE_ID")
    private CustomFieldInstance customFieldInstance;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "PERIOD_START_DATE")
    private Date periodStartDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "PERIOD_END_DATE")
    private Date periodEndDate;

    @Column(name = "STRING_VALUE")
    private String stringValue;

    @Column(name = "DATE_VALUE")
    private Date dateValue;

    @Column(name = "LONG_VALUE")
    private Long longValue;

    @Column(name = "DOUBLE_VALUE")
    private Double doubleValue;

    @Column(name = "PRIORITY")
    private int priority;

    public CustomFieldInstance getCustomFieldInstance() {
        return customFieldInstance;
    }

    public void setCustomFieldInstance(CustomFieldInstance customFieldInstance) {
        this.customFieldInstance = customFieldInstance;
    }

    public Date getPeriodStartDate() {
        return periodStartDate;
    }

    public void setPeriodStartDate(Date periodStartDate) {
        this.periodStartDate = periodStartDate;
    }

    public Date getPeriodEndDate() {
        return periodEndDate;
    }

    public void setPeriodEndDate(Date periodEndDate) {
        this.periodEndDate = periodEndDate;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public Date getDateValue() {
        return dateValue;
    }

    public void setDateValue(Date dateValue) {
        this.dateValue = dateValue;
    }

    public Long getLongValue() {
        return longValue;
    }

    public void setLongValue(Long longValue) {
        this.longValue = longValue;
    }

    public Double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(Double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * Check if date falls within period start and end dates
     * 
     * @param date Date to check
     * @return True/false
     */
    public boolean isCorrespondsToPeriod(Date date) {
        return date.compareTo(periodStartDate) >= 0 && date.before(periodEndDate);
    }

    /**
     * Check if dates match period start and end dates (strict match) or overlap period start and end dates (non-strict match)
     * 
     * @param startDate
     * @param endDate
     * @param strictMatch
     * @return
     */
    public boolean isCorrespondsToPeriod(Date startDate, Date endDate, boolean strictMatch) {

        if (strictMatch) {
            boolean match = (startDate == null && periodStartDate == null) || (startDate != null && periodStartDate != null && startDate.equals(periodStartDate));
            match = match && (endDate == null && periodEndDate == null) || (endDate != null && periodEndDate != null && endDate.equals(periodEndDate));
            return match;
        }
        // Check non-strict match case when dates overlap
        if (startDate == null && endDate == null) {
            return true;
        }

        // Period is not after dates being checked
        if (startDate == null && (periodStartDate == null || periodStartDate.compareTo(endDate) < 0)) {
            return true;

            // Period is not before dates being checked
        } else if (endDate == null && (periodEndDate == null || periodEndDate.compareTo(startDate) >= 0)) {
            return true;

            // Dates are not after period
        } else if (periodStartDate == null && (startDate == null || startDate.compareTo(endDate) < 0)) {
            return true;

            // Dates are not before period
        } else if (periodEndDate == null && (endDate == null || endDate.compareTo(startDate) >= 0)) {
            return true;

        } else if (startDate != null && endDate != null && periodStartDate != null && periodEndDate != null) {

            // Dates end or start within the period
            if ((endDate.compareTo(periodEndDate) < 0 && endDate.compareTo(periodStartDate) > 0)
                    || (startDate.compareTo(periodEndDate) < 0 && startDate.compareTo(periodStartDate) > 0)) {
                return true;
            }

            // Period end or start within the dates
            if ((periodEndDate.compareTo(endDate) < 0 && periodEndDate.compareTo(startDate) > 0)
                    || (periodStartDate.compareTo(endDate) < 0 && periodStartDate.compareTo(startDate) > 0)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get a value
     * 
     * @return
     */
    public Object getValue() {
        if (stringValue != null) {
            return stringValue;
        } else if (doubleValue != null) {
            return doubleValue;
        } else if (dateValue != null) {
            return dateValue;
        } else if (longValue != null) {
            return longValue;
        }
        return null;
    }

    /**
     * Set value of a given type
     * 
     * @param value
     * @param fieldType
     */
    public void setValue(Object value, CustomFieldTypeEnum fieldType) {

        switch (fieldType) {
        case DATE:
            dateValue = (Date) value;
            break;

        case DOUBLE:
            doubleValue = (Double) value;
            break;

        case LONG:
            longValue = (Long) value;
            break;

        case STRING:
        case LIST:
            stringValue = (String) value;
        }
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof CustomFieldPeriod)) {
            return false;
        }
        return isCorrespondsToPeriod(((CustomFieldPeriod) obj).getPeriodStartDate(), ((CustomFieldPeriod) obj).getPeriodEndDate(), true);
    }
}