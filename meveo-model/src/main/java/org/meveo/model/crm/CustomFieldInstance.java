package org.meveo.model.crm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.meveo.model.AccountEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ProviderlessEntity;
import org.meveo.model.billing.InvoiceSubcategoryCountry;
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
public class CustomFieldInstance extends ProviderlessEntity {

    private static final long serialVersionUID = 8691447585410651639L;
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public CustomFieldInstance() {
        super();
        valuePeriods = new ArrayList<CustomFieldPeriod>();
    }
    
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
    
    @Column(name="ENTITY_VALUE")
    private String entityValue;
    
    @Transient
    private String label;
    @Transient
    private BusinessEntity businessEntity;
    @Transient
    private List<BusinessEntity> entityList=new ArrayList<BusinessEntity>();
    @Transient
    private Map<String,BusinessEntity> entityMap=new HashMap<String,BusinessEntity>();
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "CRM_CUSTOM_FIELD_INST_STR_MAP")
    private Map<String, String> stringMap = new HashMap<String, String>();
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "CRM_CUSTOM_FIELD_INST_STR_LIST")
    private Set<String> stringList=new HashSet<String>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "CRM_CUSTOM_FIELD_INST_DATE_LIST")
    private Set<Date> dateList=new HashSet<Date>();
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "CRM_CUSTOM_FIELD_INST_DATE_MAP")
    private Map<String,Date> dateMap=new HashMap<String, Date>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "CRM_CUSTOM_FIELD_INST_LONG_LIST")
    private Set<Long> longList=new HashSet<Long>();
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "CRM_CUSTOM_FIELD_INST_LONG_MAP")
    private Map<String,Long> longMap=new HashMap<String, Long>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "CRM_CUSTOM_FIELD_INST_DOUB_LIST")
    private Set<Double> doubleList=new HashSet<Double>();
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "CRM_CUSTOM_FIELD_INST_DOUB_MAP")
    private Map<String,Double> doubleMap=new HashMap<String, Double>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CALENDAR_ID")
    private Calendar calendar;

    @OneToMany(mappedBy = "customFieldInstance", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<CustomFieldPeriod> valuePeriods = new ArrayList<CustomFieldPeriod>();
    
    @Column(name = "DISABLED", nullable = false)
	private boolean disabled;

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
            // If value is null, don't create a new period -just nullify existing value if period exists already
            CustomFieldPeriod period = getValuePeriod(valueDate, value != null);
            if (period != null) {
                period.setStringValue(value);
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
                period.setStringValue(value);
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
            // If value is null, don't create a new period -just nullify existing value if period exists already
            CustomFieldPeriod period = getValuePeriod(valueDate, value != null);
            if (period != null) {
                period.setDateValue(value);
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
                period.setDateValue(value);
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
            // If value is null, don't create a new period -just nullify existing value if period exists already
            CustomFieldPeriod period = getValuePeriod(valueDate, value != null);
            if (period != null) {
                period.setLongValue(value);
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
                period.setLongValue(value);
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
            // If value is null, don't create a new period -just nullify existing value if period exists already
            CustomFieldPeriod period = getValuePeriod(valueDate, value != null);
            if (period != null) {
                period.setDoubleValue(value);
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
            CustomFieldPeriod period = getValuePeriod(valueDateFrom, valueDateTo, true,value != null);
            if (period != null) {
                period.setDoubleValue(value);
            }
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

    public CustomFieldPeriod addValuePeriod(Date date, Object value, CustomFieldTypeEnum fieldType) {
        CustomFieldPeriod period = getValuePeriod(date, true);
        period.setValue(value, fieldType);
        return period;
    }

    public CustomFieldPeriod addValuePeriod(Date startDate, Date endDate, Object value, CustomFieldTypeEnum fieldType) {
        CustomFieldPeriod period = getValuePeriod(startDate, endDate, true, true);
        period.setValue(value, fieldType);
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

    public static CustomFieldInstance fromTemplate(CustomFieldTemplate template) {
        CustomFieldInstance cfi = new CustomFieldInstance();
        cfi.setCode(template.getCode());
        cfi.setDescription(template.getDescription());
        cfi.setVersionable(template.isVersionable());
        cfi.setCalendar(template.getCalendar());
        // Set a default value
        if (!template.isVersionable()) {
            cfi.setValue(template.getDefaultValueConverted(), template.getFieldType());
        }

        return cfi;
    }

    public String getEntityValue() {
		return entityValue;
	}

	public void setEntityValue(String entityValue) {
		this.entityValue = entityValue;
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
            break;
        case ENTITY:
        	this.entityValue=(String)value;
        	break;
        }
    }

    public boolean isValueEmpty() {
        return (!isVersionable() && (stringValue == null && dateValue == null && longValue == null && doubleValue == null&&entityValue==null
        		&&(stringList==null||stringList.size()==0)&&(dateList==null||dateList.size()==0)&&(longList==null||longList.size()==0)&&(doubleList==null||doubleList.size()==0)
        		&&(entityList==null||entityList.size()==0)&&(stringMap==null||stringMap.size()==0)&&(dateMap==null||dateMap.size()==0)&&(longMap==null||longMap.size()==0)
        		&&(doubleMap==null||doubleMap.size()==0)&&(entityMap==null||entityMap.size()==0))) || (isVersionable() && valuePeriods.isEmpty());
        // TODO check that period values are empty
    }

    @Override
    public String toString() {
        final int maxLen = 10;
        return String
            .format(
                "CustomFieldInstance [%s, account=%s, subscription=%s, chargeTemplate=%s, serviceTemplate=%s, offerTemplate=%s, access=%s, jobInstance=%s, stringValue=%s, dateValue=%s, longValue=%s, doubleValue=%s, versionable=%s, calendar=%s, valuePeriods=%s]",
                super.toString(), account != null ? account.getId() : null, subscription != null ? subscription.getId() : null, chargeTemplate != null ? chargeTemplate.getId()
                        : null, serviceTemplate != null ? serviceTemplate.getId() : null, offerTemplate != null ? offerTemplate.getId() : null, access != null ? access.getId()
                        : null, jobInstance != null ? jobInstance.getId() : null, stringValue, dateValue, longValue, doubleValue, versionable,
                calendar != null ? calendar.getCode() : null, valuePeriods != null ? valuePeriods.subList(0, Math.min(valuePeriods.size(), maxLen)) : null);
    }

	public List<BusinessEntity> getEntityList() {
		return entityList;
	}

	public void setEntityList(List<BusinessEntity> entityList) {
		this.entityList = entityList;
	}

	public Map<String, BusinessEntity> getEntityMap() {
		return entityMap;
	}

	public void setEntityMap(Map<String, BusinessEntity> entityMap) {
		this.entityMap = entityMap;
	}

	public Map<String, String> getStringMap() {
		return stringMap;
	}

	public void setStringMap(Map<String, String> stringMap) {
		this.stringMap = stringMap;
	}

	public Set<String> getStringList() {
		return stringList;
	}

	public void setStringList(Set<String> stringList) {
		this.stringList = stringList;
	}
	
	public Set<Date> getDateList() {
		return dateList;
	}

	public void setDateList(Set<Date> dateList) {
		this.dateList = dateList;
	}

	public Map<String, Date> getDateMap() {
		return dateMap;
	}

	public void setDateMap(Map<String, Date> dateMap) {
		this.dateMap = dateMap;
	}

	public Set<Long> getLongList() {
		return longList;
	}

	public void setLongList(Set<Long> longList) {
		this.longList = longList;
	}

	public Map<String, Long> getLongMap() {
		return longMap;
	}

	public void setLongMap(Map<String, Long> longMap) {
		this.longMap = longMap;
	}

	public Set<Double> getDoubleList() {
		return doubleList;
	}

	public void setDoubleList(Set<Double> doubleList) {
		this.doubleList = doubleList;
	}

	public Map<String, Double> getDoubleMap() {
		return doubleMap;
	}

	public void setDoubleMap(Map<String, Double> doubleMap) {
		this.doubleMap = doubleMap;
	}

	public void setBusinessEntity(BusinessEntity businessEntity) {
		this.businessEntity = businessEntity;
	}

	public BusinessEntity getBusinessEntity() {
		return businessEntity;
	}
	
	//entity for list or map
	public void addEntityTolist(){
		this.entityList.add(businessEntity);
		this.businessEntity=null;
	}

	public void addEntityTolists(BusinessEntity entity){
		this.entityList.add(entity);
	}

	public void removeEntityFromlist(BusinessEntity businessEntity){
		this.entityList.remove(businessEntity);
	}

	public void addEntityTomap(){
		this.entityMap.put(label, this.businessEntity);
		this.label=null;
		this.businessEntity=null;
	}

	public void addEntityTomaps(String label,BusinessEntity businessEntity){
		this.entityMap.put(label, businessEntity);
	}

	public void removeEntityFrommap(String key){
		this.entityMap.remove(key);
	}

	//string for list or map
	public void addStringTolist(){
		this.stringList.add(stringValue);
		this.stringValue=null;
	}

	public void removeStringFromlist(String value){
		this.stringList.remove(value);
	}

	public void addStringTomap(){
		this.stringMap.put(label, this.stringValue);
		this.label=null;
		this.stringValue=null;
	}

	public void removeStringFrommap(String key){
		this.stringMap.remove(key);
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void addDateTolist(){
		this.dateList.add(dateValue);
		dateValue=null;
	}

	public void removeDateFromlist(Date value){
		this.dateList.remove(value);
	}

	public void addDateTomap(){
		this.dateMap.put(label, dateValue);
		label=null;
		dateValue=null;
	}

	public void removeDateFrommap(String key){
		this.dateMap.remove(key);
	}

	public void addLongTolist(){
		this.longList.add(longValue);
		longValue=null;
	}

	public void removeLongFromlist(Long value){
		this.longList.remove(value);
	}

	public void addLongTomap(){
		this.longMap.put(label, longValue);
		this.label=null;
		this.longValue=null;
	}

	public void addDoubleTolist(){
		this.doubleList.add(doubleValue);
		this.doubleValue=null;
	}

	public void removeDoubleTolist(Double value){
		this.doubleList.remove(value);
	}

	public void addDoubleTomap(){
		this.doubleMap.put(label, doubleValue);
		this.label=null;
		this.doubleValue=null;
	}

	public void removeDoubleFrommap(String key){
		this.doubleMap.remove(key);
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
	
	
}
