package org.meveo.model.crm;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.meveo.model.AccountEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.billing.Subscription;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.mediation.Access;

@Entity
@ExportIdentifier({ "code", "subscription.code", "account.code", "chargeTemplate.code", "serviceTemplate.code", "offerTemplate.code", "access.accessUserId",
        "access.subscription.code", "jobInstance.code", "provider" })
@Table(name = "CRM_CUSTOM_FIELD_INST", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "SUBSCRIPTION_ID", "ACCOUNT_ID", "CHARGE_TEMPLATE_ID", "SERVICE_TEMPLATE_ID",
        "OFFER_TEMPLATE_ID", "ACCESS_ID", "JOB_INSTANCE_ID", "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CRM_CUSTOM_FIELD_INST_SEQ")
public class CustomFieldInstance extends BusinessEntity {

    private static final long serialVersionUID = 8691447585410651639L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCOUNT_ID")
    private AccountEntity account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUBSCRIPTION_ID")
    private Subscription subscription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHARGE_TEMPLATE_ID")
    private ChargeTemplate chargeTemplate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SERVICE_TEMPLATE_ID")
    private ServiceTemplate serviceTemplate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OFFER_TEMPLATE_ID")
    private OfferTemplate offerTemplate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCESS_ID")
    private Access access;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JOB_INSTANCE_ID")
    private JobInstance jobInstance;

    @Column(name = "STRING_VALUE")
    private String stringValue;

    @Column(name = "DATE_VALUE")
    private Date dateValue;

    @Column(name = "LONG_VALUE")
    private Long longValue;

    @Column(name = "DOUBLE_VALUE")
    private Double doubleValue;

    @Column(name = "VERSIONABLE")
    private boolean versionable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CALENDAR_ID")
    private Calendar calendar;

    @OneToMany(mappedBy = "customFieldInstance", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<CustomFieldPeriod> customFieldPeriods = new ArrayList<CustomFieldPeriod>();

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

    public AccountEntity getAccount() {
        return account;
    }

    public void setAccount(AccountEntity account) {
        this.account = account;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public Access getAccess() {
        return access;
    }

    public void setAccess(Access access) {
        this.access = access;
    }

    public ChargeTemplate getChargeTemplate() {
        return chargeTemplate;
    }

    public void setChargeTemplate(ChargeTemplate chargeTemplate) {
        this.chargeTemplate = chargeTemplate;
    }

    public ServiceTemplate getServiceTemplate() {
        return serviceTemplate;
    }

    public void setServiceTemplate(ServiceTemplate serviceTemplate) {
        this.serviceTemplate = serviceTemplate;
    }

    public OfferTemplate getOfferTemplate() {
        return offerTemplate;
    }

    public void setOfferTemplate(OfferTemplate offerTemplate) {
        this.offerTemplate = offerTemplate;
    }

   

    /**
	 * @return the jobInstance
	 */
	public JobInstance getJobInstance() {
		return jobInstance;
	}

	/**
	 * @param jobInstance the jobInstance to set
	 */
	public void setJobInstance(JobInstance jobInstance) {
		this.jobInstance = jobInstance;
	}

	public List<CustomFieldPeriod> getCustomFieldPeriods() {
        return customFieldPeriods;
    }

    public void setCustomFieldPeriods(List<CustomFieldPeriod> customFieldPeriods) {
        this.customFieldPeriods = customFieldPeriods;
    }

    /**
     * Get string value for a given date. If values are versioned, a matching period will be searched for.
     * 
     * @param valueDate Date
     * @return A value or a versioned value
     */
    public String getStringValue(Date valueDate) {
        if (versionable) {
            CustomFieldPeriod period = getPeriod(valueDate, false);
            if (period != null) {
                return period.getStringValue();
            }
            return null;

        } else {
            return getStringValue();
        }
    }

    /**
     * Set string value for a given date. If value is versioned with a help of a calendar, a period will be created if does not exist yet.
     * 
     * @param value Value to set
     * @param valueDate Date of a value
     * @throws RuntimeException If versionable and calendar is not provided. A method setXX(value, dateFrom, dateTo) should be used.
     */
    public void setStringValue(String value, Date valueDate) {
        if (!versionable) {
            setStringValue(value);

        } else if (calendar == null) {
            throw new RuntimeException("Can not determine a period for Custom Field value if no calendar is provided");

        } else {
            CustomFieldPeriod period = getPeriod(valueDate, true);
            period.setStringValue(value);
        }
    }

    /**
     * Set string value for a given date period.
     * 
     * @param value Value to set
     * @param valueDateFrom Period start date
     * @param valueDateTo Period end date
     */
    public void setStringValue(String value, Date valueDateFrom, Date valueDateTo) {
        if (!versionable) {
            setStringValue(value);

        } else {
            CustomFieldPeriod period = getPeriod(valueDateFrom, valueDateTo, true);
            period.setStringValue(value);
        }
    }

    /**
     * Get date value for a given date. If values are versioned, a matching period will be searched for.
     * 
     * @param valueDate Date
     * @return A value or a versioned value
     */
    public Date getDateValue(Date valueDate) {
        if (versionable) {
            CustomFieldPeriod period = getPeriod(valueDate, false);
            if (period != null) {
                return period.getDateValue();
            }
            return null;

        } else {
            return getDateValue();
        }
    }

    /**
     * Set date value for a given date. If value is versioned with a help of a calendar, a period will be created if does not exist yet.
     * 
     * @param value Value to set
     * @param valueDate Date of a value
     * @throws RuntimeException If versionable and calendar is not provided. A method setXX(value, dateFrom, dateTo) should be used.
     */
    public void setDateValue(Date value, Date valueDate) {
        if (!versionable) {
            setDateValue(value);

        } else if (calendar == null) {
            throw new RuntimeException("Can not determine a period for Custom Field value if no calendar is provided");

        } else {
            CustomFieldPeriod period = getPeriod(valueDate, true);
            period.setDateValue(value);
        }
    }

    /**
     * Set date value for a given date period.
     * 
     * @param value Value to set
     * @param valueDateFrom Period start date
     * @param valueDateTo Period end date
     */
    public void setDateValue(Date value, Date valueDateFrom, Date valueDateTo) {
        if (!versionable) {
            setDateValue(value);

        } else {
            CustomFieldPeriod period = getPeriod(valueDateFrom, valueDateTo, true);
            period.setDateValue(value);
        }
    }

    /**
     * Get long value for a given date. If values are versioned, a matching period will be searched for.
     * 
     * @param valueDate Date
     * @return A value or a versioned value
     */
    public Long getLongValue(Date valueDate) {
        if (versionable) {
            CustomFieldPeriod period = getPeriod(valueDate, false);
            if (period != null) {
                return period.getLongValue();
            }
            return null;

        } else {
            return getLongValue();
        }
    }

    /**
     * Set long value for a given date. If value is versioned with a help of a calendar, a period will be created if does not exist yet.
     * 
     * @param value Value to set
     * @param valueDate Date of a value
     * @throws RuntimeException If versionable and calendar is not provided. A method setXX(value, dateFrom, dateTo) should be used.
     */
    public void setLongValue(Long value, Date valueDate) {
        if (!versionable) {
            setLongValue(value);

        } else if (calendar == null) {
            throw new RuntimeException("Can not determine a period for Custom Field value if no calendar is provided");

        } else {
            CustomFieldPeriod period = getPeriod(valueDate, true);
            period.setLongValue(value);
        }
    }

    /**
     * Set long value for a given date period.
     * 
     * @param value Value to set
     * @param valueDateFrom Period start date
     * @param valueDateTo Period end date
     */
    public void setLongValue(Long value, Date valueDateFrom, Date valueDateTo) {
        if (!versionable) {
            setLongValue(value);

        } else {
            CustomFieldPeriod period = getPeriod(valueDateFrom, valueDateTo, true);
            period.setLongValue(value);
        }
    }

    /**
     * Get double value for a given date. If values are versioned, a matching period will be searched for.
     * 
     * @param valueDate Date
     * @return A value or a versioned value
     */
    public Double getDoubleValue(Date valueDate) {
        if (versionable) {
            CustomFieldPeriod period = getPeriod(valueDate, false);
            if (period != null) {
                return period.getDoubleValue();
            }
            return null;

        } else {
            return getDoubleValue();
        }
    }

    /**
     * Set double value for a given date. If value is versioned with a help of a calendar, a period will be created if does not exist yet.
     * 
     * @param value Value to set
     * @param valueDate Date of a value
     * @throws RuntimeException If versionable and calendar is not provided. A method setXX(value, dateFrom, dateTo) should be used.
     */
    public void setDoubleValue(Double value, Date valueDate) {
        if (!versionable) {
            setDoubleValue(value);

        } else if (calendar == null) {
            throw new RuntimeException("Can not determine a period for Custom Field value if no calendar is provided");

        } else {
            CustomFieldPeriod period = getPeriod(valueDate, true);
            period.setDoubleValue(value);
        }
    }

    /**
     * Set double value for a given date period.
     * 
     * @param value Value to set
     * @param valueDateFrom Period start date
     * @param valueDateTo Period end date
     */
    public void setDoubleValue(Double value, Date valueDateFrom, Date valueDateTo) {
        if (!versionable) {
            setDoubleValue(value);

        } else {
            CustomFieldPeriod period = getPeriod(valueDateFrom, valueDateTo, true);
            period.setDoubleValue(value);
        }
    }

    public String toJson() {
        String result = code + ":";

        if (stringValue != null) {
            result += "'" + stringValue + "'";
        } else if (dateValue != null) {
            result += "'" + sdf.format(dateValue) + "'";
        } else if (longValue != null) {
            result += longValue;
        } else if (doubleValue != null) {
            result += doubleValue;
        } else {
            result = "";
        }

        return result;
    }

    public String getValueAsString() {
        String result = "";

        if (stringValue != null) {
            result += stringValue;
        } else if (dateValue != null) {
            result += sdf.format(dateValue);
        } else if (longValue != null) {
            result += longValue;
        } else if (doubleValue != null) {
            result += doubleValue;
        } else {
            result = "";
        }

        return result;
    }

    /**
     * Get a period corresponding to a given date. Calendar is used to determine period start/end dates if requested to create one if not found
     * 
     * @param date Date
     * @param createIfNotFound Should period be created if not found
     * @return Custom field period
     */
    private CustomFieldPeriod getPeriod(Date date, Boolean createIfNotFound) {
        CustomFieldPeriod periodFound = null;
        for (CustomFieldPeriod period : customFieldPeriods) {
            if (period.isCorrespondsToPeriod(date)) {
                // If calendar is used for versioning, then no periods can overlap
                if (calendar != null) {
                    periodFound = period;
                    break;
                    // Otherwise match the period with highest priority
                } else if (periodFound == null || periodFound.getPriority() < period.getPriority()) {
                    periodFound = period;
                }
            }
        }

        if (periodFound == null && createIfNotFound && calendar != null) {
            periodFound = new CustomFieldPeriod();
            periodFound.setCustomFieldInstance(this);
            periodFound.setPeriodEndDate(calendar.nextCalendarDate(date));
            periodFound.setPeriodStartDate(calendar.previousCalendarDate(date));
            customFieldPeriods.add(periodFound);

        }
        return periodFound;
    }

    /**
     * Get a period corresponding to a given start and end date
     * 
     * @param date Date
     * @param createIfNotFound Should period be created if not found
     * @param calendar Calendar to determine period start/end dates when creating a new period
     * @return Custom field period
     */
    private CustomFieldPeriod getPeriod(Date startDate, Date endDate, Boolean createIfNotFound) {
        CustomFieldPeriod periodFound = null;
        for (CustomFieldPeriod period : customFieldPeriods) {
            if (period.isCorrespondsToPeriod(startDate, endDate)) {
                periodFound = period;
                break;
            }
        }

        if (periodFound == null && createIfNotFound) {
            periodFound = new CustomFieldPeriod();
            periodFound.setCustomFieldInstance(this);
            periodFound.setPeriodEndDate(endDate);
            periodFound.setPeriodStartDate(startDate);
            periodFound.setPriority(getNextPriority());
            customFieldPeriods.add(periodFound);

        }
        return periodFound;
    }

    public void setVersionable(boolean versionable) {
        this.versionable = versionable;
    }

    public boolean isVersionable() {
        return versionable;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    private int getNextPriority() {
        int maxPriority = 0;
        for (CustomFieldPeriod period : customFieldPeriods) {
            maxPriority = (period.getPriority() > maxPriority ? period.getPriority() : maxPriority);
        }
        return maxPriority + 1;
    }

    @Override
    public String toString() {
        return "CustomFieldInstance [account=" + account + ", subscription=" + subscription + ", chargeTemplate=" + chargeTemplate + ", serviceTemplate=" + serviceTemplate
                + ", offerTemplate=" + offerTemplate + ", access=" + access + ", JobInstance=" + jobInstance + ", stringValue=" + stringValue + ", " + "dateValue=" + dateValue
                + ", longValue=" + longValue + ", doubleValue=" + doubleValue + "]";
    }
}
