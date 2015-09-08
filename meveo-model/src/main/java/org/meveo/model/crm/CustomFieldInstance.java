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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.meveo.model.AccountEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.IEntity;
import org.meveo.model.ProviderlessEntity;
import org.meveo.model.billing.Subscription;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.mediation.Access;

@Entity
@ExportIdentifier({ "code", "subscription.code", "subscription.provider", "account.code", "account.provider", "chargeTemplate.code", "chargeTemplate.provider",
        "serviceTemplate.code", "serviceTemplate.provider", "offerTemplate.code", "offerTemplate.provider", "access.accessUserId", "access.subscription.code", "access.provider",
        "jobInstance.code", "jobInstance.provider", "provider" })
@Table(name = "CRM_CUSTOM_FIELD_INST", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "SUBSCRIPTION_ID", "ACCOUNT_ID", "CHARGE_TEMPLATE_ID", "SERVICE_TEMPLATE_ID",
        "OFFER_TEMPLATE_ID", "ACCESS_ID", "JOB_INSTANCE_ID", "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CRM_CUSTOM_FIELD_INST_SEQ")
@NamedQueries({ @NamedQuery(name = "CustomFieldInstance.getCFIForCache", query = "SELECT cfi from CustomFieldInstance cfi JOIN FETCH cfi.account where cfi.account is not null and cfi.disabled=false "
        + "UNION SELECT cfi from CustomFieldInstance cfi JOIN FETCH cfi.provider where cfi.provider is not null and cfi.disabled=false "
        + "UNION SELECT cfi from CustomFieldInstance cfi JOIN FETCH cfi.subscription where cfi.subscription is not null and cfi.disabled=false "
        + "UNION SELECT cfi from CustomFieldInstance cfi JOIN FETCH cfi.chargeTemplate where cfi.chargeTemplate is not null and cfi.disabled=false "
        + "UNION SELECT cfi from CustomFieldInstance cfi JOIN FETCH cfi.serviceTemplate where cfi.serviceTemplate is not null and cfi.disabled=false "
        + "UNION SELECT cfi from CustomFieldInstance cfi JOIN FETCH cfi.offerTemplate where cfi.offerTemplate is not null and cfi.disabled=false "
        + "UNION SELECT cfi from CustomFieldInstance cfi JOIN FETCH cfi.access where cf.access is not null and cfi.disabled=false "
        + "UNION SELECT cfi from CustomFieldInstance cfi JOIN FETCH cfi.jobInstance where cfi.jobInstance is not null and cfi.disabled=false ") })
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
    private CustomFieldValue cfValue;

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
        cfValue = new CustomFieldValue();
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
        return getCfValue().getStringValue();
    }

    public void setStringValue(String stringValue) {
        getCfValue().setStringValue(stringValue);
    }

    public Date getDateValue() {
        return getCfValue().getDateValue();
    }

    public void setDateValue(Date dateValue) {
        getCfValue().setDateValue(dateValue);
    }

    public Long getLongValue() {
        return getCfValue().getLongValue();
    }

    public void setLongValue(Long longValue) {
        getCfValue().setLongValue(longValue);
    }

    public Double getDoubleValue() {
        return getCfValue().getDoubleValue();
    }

    public void setDoubleValue(Double doubleValue) {
        getCfValue().setDoubleValue(doubleValue);
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
                return period.getCfValue().getStringValue();
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
                period.getCfValue().setStringValue(value);
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
                period.getCfValue().setStringValue(value);
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
                return period.getCfValue().getDateValue();
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
                period.getCfValue().setDateValue(value);
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
                period.getCfValue().setDateValue(value);
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
                return period.getCfValue().getLongValue();
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
                period.getCfValue().setLongValue(value);
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
                period.getCfValue().setLongValue(value);
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
                return period.getCfValue().getDoubleValue();
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
                period.getCfValue().setDoubleValue(value);
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
                period.getCfValue().setDoubleValue(value);
            }
        }
    }

    public List<Object> getListValue() {
        return getCfValue().getListValue();
    }

    public void setListValue(List<Object> listValue) {
        getCfValue().setListValue(listValue);
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
                return period.getCfValue().getListValue();
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
                period.getCfValue().setListValue(listValue);
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
                period.getCfValue().setListValue(listValue);
            }
        }
    }

    public Map<String, Object> getMapValue() {
        return getCfValue().getMapValue();
    }

    public void setMapValue(Map<String, Object> mapValue) {
        getCfValue().setMapValue(mapValue);
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
                return period.getCfValue().getMapValue();
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
                period.getCfValue().setMapValue(mapValue);
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
                period.getCfValue().setMapValue(mapValue);
            }
        }
    }

    public EntityReferenceWrapper getEntityReferenceValue() {
        return getCfValue().getEntityReferenceValue();
    }

    public void setEntityReferenceValue(EntityReferenceWrapper entityReference) {
        getCfValue().setEntityReferenceValue(entityReference);
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
                return period.getCfValue().getEntityReferenceValue();
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
                period.getCfValue().setEntityReferenceValue(entityReference);
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
                period.getCfValue().setEntityReferenceValue(entityReference);
            }
        }
    }

    public String toJson() {
        String result = code + ":";
        if (versionable) {

        } else {
            result += getCfValue().toJson(sdf);
        }

        return result;
    }

    /**
     * Get value. A generic way to retrieve a value, not knowing of its type beforehand
     * 
     * @return A non-versioned value
     */
    public Object getValue() {
        if (!versionable) {
            return getCfValue().getValue();
        }
        return null;
    }

    /**
     * Get value for a given date. If values are versioned, a matching period will be searched for.
     * 
     * @param valueDate Date
     * @return A value or a versioned value
     */
    public Object getValue(Date valueDate) {
        if (versionable) {
            CustomFieldPeriod period = getValuePeriod(valueDate, false);
            if (period != null) {
                return period.getCfValue().getValue();
            }
            return null;

        } else {
            return getValue();
        }
    }

    /**
     * Set value. A generic way to set a value. What field to populate determines by a value data type.
     * 
     * @param value Value to set
     */
    public void setValue(Object value) {
        if (value == null) {
            this.cfValue = new CustomFieldValue();
        } else {
            getCfValue().setValue(value);
        }
    }

    /**
     * Set value for a given date. If value is versioned with a help of a calendar, a period will be created if does not exist yet. A generic way to set a value. What field to
     * populate determines by a value data type.
     * 
     * @param value Value to set
     * @param valueDate Date of a value
     * @throws RuntimeException If versionable and calendar is not provided. A method setXX(value, dateFrom, dateTo) should be used.
     */
    public void setValue(Object value, Date valueDate) {
        if (!versionable) {
            setValue(value);

        } else if (calendar == null) {
            throw new RuntimeException("Can not determine a period for Custom Field value if no calendar is provided");

        } else {
            // If value is null, don't create a new period -just nullify existing value if period exists already
            CustomFieldPeriod period = getValuePeriod(valueDate, value != null);
            if (period != null) {
                period.getCfValue().setValue(value);
            }
        }
    }

    /**
     * Set value for a given date period. A generic way to set a value. What field to populate is determined by a value data type.
     * 
     * @param value Value to set
     * @param valueDateFrom Period start date
     * @param valueDateTo Period end date
     */
    public void setValue(Object value, Date valueDateFrom, Date valueDateTo) {
        if (!versionable) {
            setValue(value);

        } else {
            // If value is null, don't create a new period -just nullify existing value if period exists already
            CustomFieldPeriod period = getValuePeriod(valueDateFrom, valueDateTo, true, value != null);
            if (period != null) {
                period.getCfValue().setValue(value);
            }
        }
    }

    public String getValueAsString() {
        if (versionable) {
            return null;
        } else {
            return getCfValue().getValueAsString(sdf);
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
                if (periodFound == null || periodFound.getPriority() < period.getPriority()) {
                    periodFound = period;
                }
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
                cfi.getCfValue().setValue(cft.getDefaultValueConverted());
            }
        }
        cfi.setTriggerEndPeriodEvent(cft.isTriggerEndPeriodEvent());

        return cfi;
    }

    /**
     * Check if values is empty when used in data entry/display for GUI (use XXXForGUI fields instead of serializedValue field )
     * 
     * @return True is value is empty
     */
    public boolean isValueEmptyForGui() {
        return (!isVersionable() && getCfValue().isValueEmptyForGui()) || (isVersionable() && valuePeriods.isEmpty());
    }

    /**
     * Check if values is empty when used in non-GUI data manipulation (use serializedValue instead of XXXForGUI fields)
     * 
     * @return True is value is empty
     */
    public boolean isValueEmpty() {
        return (!isVersionable() && getCfValue().isValueEmpty()) || (isVersionable() && valuePeriods.isEmpty());
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
                valuePeriods != null ? valuePeriods.subList(0, Math.min(valuePeriods.size(), maxLen)) : null, cfValue);
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

    public CustomFieldValue getCfValue() {
        if (cfValue == null) {
            cfValue = new CustomFieldValue();
        }
        return cfValue;
    }

    public void setCfValue(CustomFieldValue cfValue) {
        this.cfValue = cfValue;
    }

    public IEntity getRelatedEntity() {

        if (provider != null) {
            return provider;

        } else if (account != null) {
            return account;

        } else if (subscription != null) {
            return subscription;

        } else if (chargeTemplate != null) {
            return chargeTemplate;

        } else if (serviceTemplate != null) {
            return serviceTemplate;

        } else if (offerTemplate != null) {
            return offerTemplate;

        } else if (access != null) {
            return access;

        } else if (jobInstance != null) {
            return jobInstance;
        }

        return null;
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
        if (cfValue != null) {
            getCfValue().deserializeValue();
        }
    }
}