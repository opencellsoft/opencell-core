package org.meveo.model.crm;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.meveo.model.BaseProviderlessEntity;

@Entity
@Table(name = "CRM_CUSTOM_FIELD_PERIOD", uniqueConstraints = @UniqueConstraint(columnNames = { "CF_INSTANCE_ID", "PERIOD_START_DATE" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CRM_CUSTOM_FIELD_PERIOD_SEQ")
@NamedQueries({ @NamedQuery(name = "CustomFieldPeriod.findByPeriodDate", query = "SELECT cp FROM CustomFieldPeriod cp WHERE cp.customFieldInstance=:customFieldInstance AND cp.periodStartDate<=:date AND cp.periodEndDate>:date"), })
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

    public boolean isCorrespondsToPeriod(Date date) {
        return date.compareTo(periodStartDate) >= 0 && date.before(periodEndDate);
    }

    public boolean isCorrespondsToPeriod(Date startDate, Date endDate) {
        boolean match = (startDate == null && periodStartDate == null) || (startDate != null && periodStartDate != null && startDate.equals(periodStartDate));
        match = match && (endDate == null && periodEndDate == null) || (endDate != null && periodEndDate != null && endDate.equals(periodEndDate));

        return match;
    }
}