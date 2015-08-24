package org.meveo.model.crm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.meveo.model.AccountEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ProviderlessEntity;
import org.meveo.model.billing.Subscription;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.mediation.Access;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@ExportIdentifier({ "code", "subscription.code", "subscription.provider", "account.code", "account.provider", "chargeTemplate.code", "chargeTemplate.provider",
        "serviceTemplate.code", "serviceTemplate.provider", "offerTemplate.code", "offerTemplate.provider", "access.accessUserId", "access.subscription.code", "access.provider",
        "jobInstance.code", "jobInstance.provider", "provider" })
@Table(name = "CRM_CUSTOM_FIELD_INST", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "SUBSCRIPTION_ID", "ACCOUNT_ID", "CHARGE_TEMPLATE_ID", "SERVICE_TEMPLATE_ID",
        "OFFER_TEMPLATE_ID", "ACCESS_ID", "JOB_INSTANCE_ID", "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CRM_CUSTOM_FIELD_INST_SEQ")
public class CustomFieldInstance extends ProviderlessEntity {

    private static final long serialVersionUID = 8691447585410651639L;
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Column(name = "CODE", nullable = false, length = 60)
    @Size(max = 60, min = 1)
    @NotNull
    private String code;

    @Column(name = "DESCRIPTION", nullable = true, length = 100)
    @Size(max = 100)
    private String description;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "PROVIDER_ID")
    private Provider provider;

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

    @Column(name = "VERSIONABLE")
    private boolean versionable;

    @Embedded
    private CustomFieldValue value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CALENDAR_ID")
    private Calendar calendar;

    @OneToMany(mappedBy = "customFieldInstance", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<CustomFieldPeriod> valuePeriods = new ArrayList<CustomFieldPeriod>();

    @Column(name = "DISABLED", nullable = false)
    private boolean disabled;

    @Column(name = "TRIGGER_END_PERIOD_EVENT", nullable = false)
    private boolean triggerEndPeriodEvent;

    public CustomFieldInstance() {
        super();
        valuePeriods = new ArrayList<CustomFieldPeriod>();
        value = new CustomFieldValue();
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isActive() {
        return !disabled;
    }

    public void setActive(boolean active) {
        setDisabled(!active);
    }

    public String getStringValue() {
        return value.getStringValue();
    }

    public void setStringValue(String stringValue) {
        this.value.setStringValue(stringValue);
    }

    public Date getDateValue() {
        return value.getDateValue();
    }

    public void setDateValue(Date dateValue) {
        this.value.setDateValue(dateValue);
    }

    public Long getLongValue() {
        return value.getLongValue();
    }

    public void setLongValue(Long longValue) {
        this.value.setLongValue(longValue);
    }

    public Double getDoubleValue() {
        return value.getDoubleValue();
    }

    public void setDoubleValue(Double doubleValue) {
        this.value.setDoubleValue(doubleValue);
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

    public List<CustomFieldPeriod> getValuePeriods() {
        return valuePeriods;
    }

    public void setValuePeriods(List<CustomFieldPeriod> valuePeriods) {
        this.valuePeriods = valuePeriods;
    }

    /**
     * Get string value for a given date. If values are versioned, a matching period will be searched for.
     * 
     * @param valueDate Date
     * @return A value or a versioned value
     */
    public String getStringValue(Date valueDate) {
        if (versionable) {
            CustomFieldPeriod period = getValuePeriod(valueDate, false);
            if (period != null) {
                return period.getValue().getStringValue();
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
            // If value is null, don't create a new period -just nullify existing value if period exists already
            CustomFieldPeriod period = getValuePeriod(valueDate, value != null);
            if (period != null) {
                period.getValue().setStringValue(value);
            }
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
            // If value is null, don't create a new period -just nullify existing value if period exists already
            CustomFieldPeriod period = getValuePeriod(valueDateFrom, valueDateTo, true, value != null);
            if (period != null) {
                period.getValue().setStringValue(value);
            }
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
            CustomFieldPeriod period = getValuePeriod(valueDate, false);
            if (period != null) {
                return period.getValue().getDateValue();
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
            // If value is null, don't create a new period -just nullify existing value if period exists already
            CustomFieldPeriod period = getValuePeriod(valueDate, value != null);
            if (period != null) {
                period.getValue().setDateValue(value);
            }
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
            // If value is null, don't create a new period -just nullify existing value if period exists already
            CustomFieldPeriod period = getValuePeriod(valueDateFrom, valueDateTo, true, value != null);
            if (period != null) {
                period.getValue().setDateValue(value);
            }
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
            CustomFieldPeriod period = getValuePeriod(valueDate, false);
            if (period != null) {
                return period.getValue().getLongValue();
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
            // If value is null, don't create a new period -just nullify existing value if period exists already
            CustomFieldPeriod period = getValuePeriod(valueDate, value != null);
            if (period != null) {
                period.getValue().setLongValue(value);
            }
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
            // If value is null, don't create a new period -just nullify existing value if period exists already
            CustomFieldPeriod period = getValuePeriod(valueDateFrom, valueDateTo, true, value != null);
            if (period != null) {
                period.getValue().setLongValue(value);
            }
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
            CustomFieldPeriod period = getValuePeriod(valueDate, false);
            if (period != null) {
                return period.getValue().getDoubleValue();
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
            // If value is null, don't create a new period -just nullify existing value if period exists already
            CustomFieldPeriod period = getValuePeriod(valueDate, value != null);
            if (period != null) {
                period.getValue().setDoubleValue(value);
            }
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
            // If value is null, don't create a new period -just nullify existing value if period exists already
            CustomFieldPeriod period = getValuePeriod(valueDateFrom, valueDateTo, true, value != null);
            if (period != null) {
                period.getValue().setDoubleValue(value);
            }
        }
    }

    public List<Object> getListValue() {
        return value.getListValue();
    }

    public void setListValue(List<Object> listValue) {
        value.setListValue(listValue);
    }

    /**
     * Get list value for a given date. If values are versioned, a matching period will be searched for.
     * 
     * @param valueDate Date
     * @return A value or a versioned value
     */
    public List<Object> getListValue(Date valueDate) {
        if (versionable) {
            CustomFieldPeriod period = getValuePeriod(valueDate, false);
            if (period != null) {
                return period.getValue().getListValue();
            }
            return null;

        } else {
            return getListValue();
        }
    }

    /**
     * Set list value for a given date. If value is versioned with a help of a calendar, a period will be created if does not exist yet.
     * 
     * @param listValue Value to set
     * @param valueDate Date of a value
     * @throws RuntimeException If versionable and calendar is not provided. A method setXX(value, dateFrom, dateTo) should be used.
     */
    public void setListValue(List<Object> listValue, Date valueDate) {
        if (!versionable) {
            setListValue(listValue);

        } else if (calendar == null) {
            throw new RuntimeException("Can not determine a period for Custom Field value if no calendar is provided");

        } else {
            // If value is null, don't create a new period -just nullify existing value if period exists already
            CustomFieldPeriod period = getValuePeriod(valueDate, listValue != null);
            if (period != null) {
                period.getValue().setListValue(listValue);
            }
        }
    }

    /**
     * Set list value for a given date period.
     * 
     * @param listValue Value to set
     * @param valueDateFrom Period start date
     * @param valueDateTo Period end date
     */
    public void setListValue(List<Object> listValue, Date valueDateFrom, Date valueDateTo) {
        if (!versionable) {
            setListValue(listValue);

        } else {
            // If value is null, don't create a new period -just nullify existing value if period exists already
            CustomFieldPeriod period = getValuePeriod(valueDateFrom, valueDateTo, true, listValue != null);
            if (period != null) {
                period.getValue().setListValue(listValue);
            }
        }
    }

    public Map<String, Object> getMapValue() {
        return value.getMapValue();
    }

    public void setMapValue(Map<String, Object> mapValue) {
        value.setMapValue(mapValue);
    }

    /**
     * Get map value for a given date. If values are versioned, a matching period will be searched for.
     * 
     * @param valueDate Date
     * @return A value or a versioned value
     */
    public Map<String, Object> getMapValue(Date valueDate) {
        if (versionable) {
            CustomFieldPeriod period = getValuePeriod(valueDate, false);
            if (period != null) {
                return period.getValue().getMapValue();
            }
            return null;

        } else {
            return getMapValue();
        }
    }

    /**
     * Set map value for a given date. If value is versioned with a help of a calendar, a period will be created if does not exist yet.
     * 
     * @param mapValue Value to set
     * @param valueDate Date of a value
     * @throws RuntimeException If versionable and calendar is not provided. A method setXX(value, dateFrom, dateTo) should be used.
     */
    public void setMapValue(Map<String, Object> mapValue, Date valueDate) {
        if (!versionable) {
            setMapValue(mapValue);

        } else if (calendar == null) {
            throw new RuntimeException("Can not determine a period for Custom Field value if no calendar is provided");

        } else {
            // If value is null, don't create a new period -just nullify existing value if period exists already
            CustomFieldPeriod period = getValuePeriod(valueDate, mapValue != null);
            if (period != null) {
                period.getValue().setMapValue(mapValue);
            }
        }
    }

    /**
     * Set map value for a given date period.
     * 
     * @param mapValue Value to set
     * @param valueDateFrom Period start date
     * @param valueDateTo Period end date
     */
    public void setMapValue(Map<String, Object> mapValue, Date valueDateFrom, Date valueDateTo) {
        if (!versionable) {
            setMapValue(mapValue);

        } else {
            // If value is null, don't create a new period -just nullify existing value if period exists already
            CustomFieldPeriod period = getValuePeriod(valueDateFrom, valueDateTo, true, mapValue != null);
            if (period != null) {
                period.getValue().setMapValue(mapValue);
            }
        }
    }

    public EntityReferenceWrapper getEntityReferenceValue() {
        return value.getEntityReferenceValue();
    }

    public void setEntityReferenceValue(EntityReferenceWrapper entityReference) {
        value.setEntityReferenceValue(entityReference);
    }

    /**
     * Get entity reference value for a given date. If values are versioned, a matching period will be searched for.
     * 
     * @param valueDate Date
     * @return A value or a versioned value
     */
    public EntityReferenceWrapper getEntityReferenceValue(Date valueDate) {
        if (versionable) {
            CustomFieldPeriod period = getValuePeriod(valueDate, false);
            if (period != null) {
                return period.getValue().getEntityReferenceValue();
            }
            return null;

        } else {
            return getEntityReferenceValue();
        }
    }

    /**
     * Set entity reference value for a given date. If value is versioned with a help of a calendar, a period will be created if does not exist yet.
     * 
     * @param entityReference Value to set
     * @param valueDate Date of a value
     * @throws RuntimeException If versionable and calendar is not provided. A method setXX(value, dateFrom, dateTo) should be used.
     */
    public void setEntityReferenceValue(EntityReferenceWrapper entityReference, Date valueDate) {
        if (!versionable) {
            setEntityReferenceValue(entityReference);

        } else if (calendar == null) {
            throw new RuntimeException("Can not determine a period for Custom Field value if no calendar is provided");

        } else {
            // If value is null, don't create a new period -just nullify existing value if period exists already
            CustomFieldPeriod period = getValuePeriod(valueDate, entityReference != null);
            if (period != null) {
                period.getValue().setEntityReferenceValue(entityReference);
            }
        }
    }

    /**
     * Set entity reference value for a given date period.
     * 
     * @param entityReference Value to set
     * @param valueDateFrom Period start date
     * @param valueDateTo Period end date
     */
    public void setEntityReferenceValue(EntityReferenceWrapper entityReference, Date valueDateFrom, Date valueDateTo) {
        if (!versionable) {
            setEntityReferenceValue(entityReference);

        } else {
            // If value is null, don't create a new period -just nullify existing value if period exists already
            CustomFieldPeriod period = getValuePeriod(valueDateFrom, valueDateTo, true, entityReference != null);
            if (period != null) {
                period.getValue().setEntityReferenceValue(entityReference);
            }
        }
    }

    public String toJson() {
        String result = code + ":";
        if (versionable) {

        } else {
            result += value.toJson(sdf);
        }

        return result;
    }

    public String getValueAsString() {
        if (versionable) {
            return null;
        } else {
            return value.getValueAsString(sdf);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        CustomFieldInstance other = (CustomFieldInstance) obj;
        if (getId() == null) {
            if (other.getId() != null)
                return false;
        } else if (!getId().equals(other.getId()))
            return false;
        return true;
    }

    public CustomFieldPeriod addValuePeriod(Date date) {
        CustomFieldPeriod period = getValuePeriod(date, true);
        return period;
    }

    public CustomFieldPeriod addValuePeriod(Date startDate, Date endDate) {
        CustomFieldPeriod period = getValuePeriod(startDate, endDate, true, true);
        return period;
    }

    /**
     * Get a period corresponding to a given date. Calendar is used to determine period start/end dates if requested to create one if not found
     * 
     * @param date Date
     * @param createIfNotFound Should period be created if not found
     * @return Custom field period
     */
    public CustomFieldPeriod getValuePeriod(Date date, Boolean createIfNotFound) {
        CustomFieldPeriod periodFound = null;
        for (CustomFieldPeriod period : valuePeriods) {
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
            valuePeriods.add(periodFound);

        }
        return periodFound;
    }

    /**
     * Get a period corresponding to a given start and end date
     * 
     * @param date Date
     * @param createIfNotFound Should period be created if not found
     * @param calendar Calendar to determine period start/end dates when creating a new period
     * @param strictMatch Should a match occur only if start and end dates match. Non-strict match would match when dates overlap
     * @return Custom field period
     */
    public CustomFieldPeriod getValuePeriod(Date startDate, Date endDate, boolean strictMatch, Boolean createIfNotFound) {
        CustomFieldPeriod periodFound = null;
        for (CustomFieldPeriod period : valuePeriods) {
            if (period.isCorrespondsToPeriod(startDate, endDate, strictMatch)) {
                periodFound = period;
                break;
            }
        }
        // Create a period if match not found
        if (periodFound == null && createIfNotFound) {
            periodFound = new CustomFieldPeriod();
            periodFound.setCustomFieldInstance(this);
            periodFound.setPeriodEndDate(endDate);
            periodFound.setPeriodStartDate(startDate);
            periodFound.setPriority(getNextPriority());
            valuePeriods.add(periodFound);

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
        for (CustomFieldPeriod period : valuePeriods) {
            maxPriority = (period.getPriority() > maxPriority ? period.getPriority() : maxPriority);
        }
        return maxPriority + 1;
    }

    public void removeValuePeriod(CustomFieldPeriod period) {
        valuePeriods.remove(period);
    }

    /**
     * Instantiate a CustomFieldInstance from a template setting a default value if applicable
     * 
     * @param cft Custom field template
     * @return CustomFieldInstance object
     */
    public static CustomFieldInstance fromTemplate(CustomFieldTemplate cft) {
        CustomFieldInstance cfi = new CustomFieldInstance();
        cfi.setCode(cft.getCode());
        cfi.setDescription(cft.getDescription());
        cfi.setVersionable(cft.isVersionable());
        cfi.setCalendar(cft.getCalendar());
        // Set a default value
        if (!cft.isVersionable()) {
            if (cft.getStorageType() == CustomFieldStorageTypeEnum.SINGLE) {
                cfi.getValue().setSingleValue(cft.getDefaultValueConverted(), cft.getFieldType());
            }
        }
        cfi.setTriggerEndPeriodEvent(cft.isTriggerEndPeriodEvent());

        return cfi;
    }

    public boolean isValueEmpty() {
        return (!isVersionable() && value.isValueEmpty()) || (isVersionable() && valuePeriods.isEmpty());
        // TODO check that period values are empty
    }

    @Override
    public String toString() {
        final int maxLen = 10;
        return String
            .format(
                "CustomFieldInstance [%s, account=%s, subscription=%s, chargeTemplate=%s, serviceTemplate=%s, offerTemplate=%s, access=%s, jobInstance=%s, versionable=%s, calendar=%s, valuePeriods=%s, value=%s]",
                super.toString(), account != null ? account.getId() : null, subscription != null ? subscription.getId() : null, chargeTemplate != null ? chargeTemplate.getId()
                        : null, serviceTemplate != null ? serviceTemplate.getId() : null, offerTemplate != null ? offerTemplate.getId() : null, access != null ? access.getId()
                        : null, jobInstance != null ? jobInstance.getId() : null, versionable, calendar != null ? calendar.getCode() : null,
                valuePeriods != null ? valuePeriods.subList(0, Math.min(valuePeriods.size(), maxLen)) : null, value);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public boolean isTriggerEndPeriodEvent() {
        return triggerEndPeriodEvent;
    }

    public void setTriggerEndPeriodEvent(boolean triggerEndPeriodEvent) {
        this.triggerEndPeriodEvent = triggerEndPeriodEvent;
    }

    public CustomFieldValue getValue() {
        return value;
    }

    public void setValue(CustomFieldValue value) {
        this.value = value;
    }

    // /**
    // * NOT WORK/NOT USED: A JPA callback method to serialise reference to entity, list and map values upon persisting to DB.
    // *
    // * On update (@PreUpdate) does not work, as merge loose all transient values before calling PreUpdate callback. EclipseLink has PostMerge callback that Hibernate does not
    // have.
    // */
    // // @PrePersist
    // // @PreUpdate
    // private void serializeValue() {
    // Logger log = LoggerFactory.getLogger(this.getClass());
    // value.serializeValue();
    // }

    /**
     * A JPA callback to deserialise reference to entity, list and map values upon retrieval from DB.
     */
    @PostLoad
    private void deserializeValue() {
        Logger log = LoggerFactory.getLogger(this.getClass());
        if (value != null) {
            value.deserializeValue();
        }
    }
}