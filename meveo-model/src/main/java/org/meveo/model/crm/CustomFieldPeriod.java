package org.meveo.model.crm;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.meveo.model.BaseProviderlessEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.crm.wrapper.BaseWrapper;
import org.meveo.model.crm.wrapper.BusinessEntityWrapper;
import org.meveo.model.crm.wrapper.DateWrapper;
import org.meveo.model.crm.wrapper.DoubleWrapper;
import org.meveo.model.crm.wrapper.LongWrapper;
import org.meveo.model.crm.wrapper.StringWrapper;

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
    
   // @Lob
    //@Basic(fetch=FetchType.LAZY)
    @Column(name="ENTITY_VALUE",nullable=true)
    private String entityValue;
    
    /**
     * label for map
     */
    @Column(name="LABEL")
    private String label;
    @Transient
    private List<BusinessEntityWrapper> entityList=new ArrayList<BusinessEntityWrapper>();
    
    @Transient
    private List<StringWrapper> stringList=new ArrayList<StringWrapper>();

    @Transient
    private List<DateWrapper> dateList=new ArrayList<DateWrapper>();
    
    @Transient
    private List<LongWrapper> longList=new ArrayList<LongWrapper>();
    
    @Transient
    private List<DoubleWrapper> doubleList=new ArrayList<DoubleWrapper>();
    
    @Transient
    private BusinessEntity businessEntity;
    
    public CustomFieldPeriod(){
    }

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
    
    

    public String getEntityValue() {
		return entityValue;
	}

	public void setEntityValue(String entityValue) {
		this.entityValue = entityValue;
	}

	public BusinessEntity getBusinessEntity() {
		return businessEntity;
	}

	public void setBusinessEntity(BusinessEntity businessEntity) {
		this.businessEntity = businessEntity;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
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
        }else if(businessEntity!=null){
        	return businessEntity;
        }else if(stringList!=null&&stringList.size()>0){
        	return stringList;
        }else if(dateList!=null&&dateList.size()>0){
        	return dateList;
        }else if(longList!=null&&longList.size()>0){
        	return longList;
        }else if(doubleList!=null&&doubleList.size()>0){
        	return doubleList;
        }else if(entityList!=null&&entityList.size()>0){
        	return entityList;
        }else if(entityValue!=null){
        	return entityValue;
        }
        return null;
    }

    /**
     * Set value of a given type
     * 
     * @param value
     * @param fieldType
     */
	@SuppressWarnings("unchecked")
	public void setValue(Object value, String label,CustomFieldTypeEnum fieldType,CustomFieldStorageTypeEnum storageType) {
    	switch(storageType){
    	case SINGLE:
        	switch (fieldType) {
        	case DATE:
        		dateValue=(Date)value;
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
        	case TEXT_AREA:
        		entityValue=(String)value;
        		break;
        	case ENTITY:
        		businessEntity=(BusinessEntity)value;
        		break;
        	default:
        		break;
        	}
        	break;
    	case MAP:
    		this.label=label;
    	case LIST:
    		switch (fieldType) {
			case DATE:
				dateList=(List<DateWrapper>)value;
    			break;
    		case DOUBLE:
    			doubleList=(List<DoubleWrapper>)value;
    			break;
    		case LONG:
    			longList=(List<LongWrapper>)value;
    			break;
    		case STRING:
    		case LIST:
    		case TEXT_AREA:
    			stringList=(List<StringWrapper>)value;
    			break;
    		case ENTITY:
    			entityList=(List<BusinessEntityWrapper>)value;
    			break;
    		default:
    			break;
    		}
    	}
    }

	public void setDefaultValue(Object value, CustomFieldTypeEnum fieldType,CustomFieldStorageTypeEnum storageType) {
		if(value==null){
			return;
		}
    	switch(storageType){
    	case SINGLE:
        	switch (fieldType) {
        	case DATE:
        		break;
        	case DOUBLE:
        		if(value!=null)
        		doubleValue = (Double) value;
        		break;
        	case LONG:
        		if(value!=null)
        		longValue = (Long) value;
        		break;
        	case STRING:
        	case LIST:
        		if(value!=null)
        		stringValue = (String) value;
        		break;
        	case TEXT_AREA:
        		if(value!=null)
        		entityValue=(String)value;
        		break;
        	case ENTITY:
        		break;
        	default:
        		break;
        	}
        	break;
    	case LIST:
    	case MAP:
    		switch (fieldType) {
			case DATE:
    			break;
    		case DOUBLE:
    			if(value!=null)
    			doubleList.add(new DoubleWrapper((Double)value));
    			break;
    		case LONG:
    			if(value!=null)
    			longList.add(new LongWrapper((Long)value));
    			break;
    		case STRING:
    		case LIST:
    		case TEXT_AREA:
    			if(value!=null)
    			stringList.add(new StringWrapper((String)value));
    			break;
    		case ENTITY:
    			break;
    		default:
    			break;
    		}
    	}
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof CustomFieldPeriod)) {
            return false;
        }
        return isCorrespondsToPeriod(((CustomFieldPeriod) obj).getPeriodStartDate(), ((CustomFieldPeriod) obj).getPeriodEndDate(), true);
    }
    public List<BusinessEntityWrapper> getEntityList() {
		return entityList;
	}

	public void setEntityList(List<BusinessEntityWrapper> entityList) {
		this.entityList = entityList;
	}
	//entity for list or map
	public void addEntityTolist(){
		this.entityList.add(new BusinessEntityWrapper());
	}
	public void addEntityTolists(BusinessEntity entity){
		this.entityList.add(new BusinessEntityWrapper(entity));
	}
	public void removeEntityFromlist(BusinessEntityWrapper value){
		this.entityList.remove(value);
	}
	public void addEntityTomap(){
		this.entityList.add(new BusinessEntityWrapper());
	}

	public void addEntityTomap(String label,BusinessEntity businessEntity){
		this.entityList.add(new BusinessEntityWrapper(label, businessEntity));
	}

	public List<StringWrapper> getStringList() {
		return stringList;
	}

	public void setStringList(List<StringWrapper> stringList) {
		this.stringList = stringList;
	}
	//string for list or map
	public void addStringTolist(CustomFieldTemplate cft){
		this.stringList.add(new StringWrapper((String)cft.getDefaultValueConverted()));
	}

	public void removeStringFromlist(StringWrapper value){
		this.stringList.remove(value);
	}

	public void addStringTomap(CustomFieldTemplate cft){
		this.stringList.add(new StringWrapper((String)cft.getDefaultValueConverted()));
	}

	public List<DateWrapper> getDateList() {
		return dateList;
	}

	public void setDateList(List<DateWrapper> dateList) {
		this.dateList = dateList;
	}

	public void addDateTolist(){
		this.dateList.add(new DateWrapper());
	}

	public void removeDateFromlist(DateWrapper value){
		this.dateList.remove(value);
	}

	public void addDateTomap(){
		this.dateList.add(new DateWrapper());
	}

	public List<LongWrapper> getLongList() {
		return longList;
	}

	public void setLongList(List<LongWrapper> longList) {
		this.longList = longList;
	}

	public void addLongTolist(CustomFieldTemplate cft){
		this.longList.add(new LongWrapper((Long)cft.getDefaultValueConverted()));
		longValue=null;
	}

	public void removeLongFromlist(LongWrapper value){
		this.longList.remove(value);
	}

	public void addLongTomap(CustomFieldTemplate cft){
		this.longList.add(new LongWrapper((Long)cft.getDefaultValueConverted()));
	}

	public List<DoubleWrapper> getDoubleList() {
		return doubleList;
	}

	public void setDoubleList(List<DoubleWrapper> doubleList) {
		this.doubleList = doubleList;
	}

	public void addDoubleTolist(CustomFieldTemplate cft){
		this.doubleList.add(new DoubleWrapper((Double)cft.getDefaultValueConverted()));
	}

	public void removeDoubleFromlist(DoubleWrapper value){
		this.doubleList.remove(value);
	}

	public void addDoubleTomap(CustomFieldTemplate cft){
		this.doubleList.add(new DoubleWrapper((Double)cft.getDefaultValueConverted()));
	}
	public List<? extends BaseWrapper> getWrapperList(CustomFieldTemplate cft){
		if(cft==null){
			return null;
		}
		List<? extends BaseWrapper> result=null;
		switch(cft.getFieldType()){
		case STRING:
		case LIST:
		case TEXT_AREA:
			result=stringList;
			break;
		case DATE:
			result=dateList;
			break;
		case LONG:
			result=longList;
			break;
		case DOUBLE:
			result=doubleList;
			break;
		case ENTITY:
			result=entityList;
			break;
		default:
		}
		return result!=null&&result.size()>5?result.subList(0, 5):result;
	}
}