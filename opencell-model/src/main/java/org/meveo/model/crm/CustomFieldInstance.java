package org.meveo.model.crm;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.EnableEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldValue;
import org.meveo.model.shared.DateUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

@Entity
@ExportIdentifier({ "appliesToEntity", "code", "periodStartDate", "periodEndDate"})
@Table(name = "CRM_CUSTOM_FIELD_INST", uniqueConstraints = @UniqueConstraint(columnNames = { "APPLIES_TO_UUID", "CODE", "PERIOD_START_DATE", "PERIOD_END_DATE" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {@Parameter(name = "sequence_name", value = "CRM_CUSTOM_FIELD_INST_SEQ"), })
@NamedQueries({
        @NamedQuery(name = "CustomFieldInstance.getCfiForCache", query = "select cfi from CustomFieldInstance cfi where cfi.disabled=false order by cfi.appliesToEntity"),
        @NamedQuery(name = "CustomFieldInstance.getCfiByCode", query = "select cfi from CustomFieldInstance cfi where cfi.appliesToEntity=:appliesToEntity and cfi.code=:code "),
        @NamedQuery(name = "CustomFieldInstance.getCfiByCodeAndDate", query = "select cfi from CustomFieldInstance cfi where cfi.appliesToEntity=:appliesToEntity and cfi.code=:code  and ((cfi.periodStartDate<=:date and :date<cfi.periodEndDate) or (cfi.periodStartDate<=:date and cfi.periodEndDate IS NULL) or (cfi.periodStartDate IS NULL and :date<cfi.periodEndDate)) order by cfi.priority desc "),
        @NamedQuery(name = "CustomFieldInstance.getCfiByCodeAndDateRange", query = "select cfi from CustomFieldInstance cfi where cfi.appliesToEntity=:appliesToEntity and cfi.code=:code  and (cfi.periodStartDate=:dateFrom and cfi.periodEndDate=:dateTo)  order by cfi.priority desc "),
        @NamedQuery(name = "CustomFieldInstance.getCfiByEntity", query = "select cfi from CustomFieldInstance cfi where cfi.appliesToEntity=:appliesToEntity "),
        @NamedQuery(name = "CustomFieldInstance.getCfiByEntityListForIndex", query = "select cfi from CustomFieldInstance cfi where cfi.appliesToEntity in :appliesToEntityList and cfi.code in (select cft.code from CustomFieldTemplate cft where cft.indexType is not null )"),
        @NamedQuery(name = "CustomFieldInstance.getCfiValueByCode", query = "select cfi.cfValue from CustomFieldInstance cfi where cfi.appliesToEntity=:appliesToEntity and cfi.code=:code "),
        @NamedQuery(name = "CustomFieldInstance.getCfiValueByCodeAndDate", query = "select cfi.cfValue from CustomFieldInstance cfi where cfi.appliesToEntity=:appliesToEntity and cfi.code=:code  and ((cfi.periodStartDate<=:date and :date<cfi.periodEndDate) or (cfi.periodStartDate<=:date and cfi.periodEndDate IS NULL) or (cfi.periodStartDate IS NULL and :date<cfi.periodEndDate)) order by cfi.priority desc ") })
public class CustomFieldInstance extends EnableEntity {

    private static final long serialVersionUID = 8691447585410651639L;

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static SimpleDateFormat xmlsdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    @Column(name = "CODE", nullable = false, length = 255)
    @Size(max = 255, min = 1)
    @NotNull
    private String code;

    @Column(name = "APPLIES_TO_UUID", nullable = false, length = 60)
    @Size(max = 60)
    @NotNull
    private String appliesToEntity;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "PERIOD_START_DATE")
    private Date periodStartDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "PERIOD_END_DATE")
    private Date periodEndDate;

    @Column(name = "PRIORITY")
    private int priority;
    
    @Column(name = "DESCRIPTION")
    private String description;

    @Embedded
    private CustomFieldValue cfValue;

    public CustomFieldInstance() {
        super();
        cfValue = new CustomFieldValue();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setAppliesToEntity(String appliesToEntity) {
        this.appliesToEntity = appliesToEntity;
    }

    public String getAppliesToEntity() {
        return appliesToEntity;
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

    public List<Object> getListValue() {
        return getCfValue().getListValue();
    }

    public void setListValue(List<Object> listValue) {
        getCfValue().setListValue(listValue);
    }

    public Map<String, Object> getMapValue() {
        return getCfValue().getMapValue();
    }

    public void setMapValue(Map<String, Object> mapValue) {
        getCfValue().setMapValue(mapValue);
    }

    public EntityReferenceWrapper getEntityReferenceValue() {
        return getCfValue().getEntityReferenceValue();
    }

    public void setEntityReferenceValue(EntityReferenceWrapper entityReference) {
        getCfValue().setEntityReferenceValue(entityReference);
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
     * Get value. A generic way to retrieve a value, not knowing of its type beforehand
     * 
     * @return A non-versioned value
     */
    public Object getValue() {
        return getCfValue().getValue();
    }

    public String getValueAsString() {
        return getCfValue().getValueAsString(sdf);
    }
    
    

    public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
    public boolean equals(Object obj) {
        
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof CustomFieldInstance)) {
            return false;
        }

        CustomFieldInstance other = (CustomFieldInstance) obj;

        if (getId() != null && other.getId() != null && getId().equals(other.getId())) {
            return true;
        }

        if (code == null && other.getCode() != null) {
            return false;
        } else if (!code.equals(other.getCode())) {
            return false;
        } else if (appliesToEntity == null && other.getAppliesToEntity() != null) {
            return false;
        } else if (!appliesToEntity.equals(other.getAppliesToEntity())) {
            return false;
        }

        return isCorrespondsToPeriod(other.getPeriodStartDate(), other.getPeriodEndDate(), true);
    }

    /**
     * Instantiate a CustomFieldInstance from a template setting a default value if applicable
     * 
     * @param cft Custom field template
     * @param entity Entity to which custom field aplies to
     * @return CustomFieldInstance object
     */
    public static CustomFieldInstance fromTemplate(CustomFieldTemplate cft, ICustomFieldEntity entity) {
        CustomFieldInstance cfi = new CustomFieldInstance();
        cfi.setCode(cft.getCode());
        cfi.setDescription(cft.getDescriptionOrCode());
        cfi.setAppliesToEntity(entity.getUuid());

        // Set a default value
        if (cft.getStorageType() == CustomFieldStorageTypeEnum.SINGLE) {
            cfi.getCfValue().setValue(cft.getDefaultValueConverted());
        }

        return cfi;
    }

    /**
     * Instantiate a CustomFieldInstance from a template setting period interval and a default value if applicable
     * 
     * @param cft Custom field template
     * @param entity Entity to which custom field applies to
     * @param valueDate Pariod validity date. Date range will be determiend from a calendar
     * @return CustomFieldInstance object
     */
    public static CustomFieldInstance fromTemplate(CustomFieldTemplate cft, ICustomFieldEntity entity, Date valueDate) {
        CustomFieldInstance cfi = CustomFieldInstance.fromTemplate(cft, entity);
        if (cft.isVersionable() && cft.getCalendar() != null) {
            cfi.setPeriodEndDate(cft.getCalendar().nextCalendarDate(valueDate));
            cfi.setPeriodStartDate(cft.getCalendar().previousCalendarDate(valueDate));
        } else if (cft.isVersionable() && cft.getCalendar() != null) {
            cfi = CustomFieldInstance.fromTemplate(cft, entity, valueDate, null, null);
        }

        return cfi;
    }

    /**
     * Instantiate a CustomFieldInstance from a template setting period interval and a default value if applicable
     * 
     * @param cft Custom field template
     * @param entity Entity to which custom field aplies to
     * @param valueDateFrom Period validity date - from
     * @param valueDateTo Period validity date - to
     * @param valuePriority Value priority
     * @return CustomFieldInstance object
     */
    public static CustomFieldInstance fromTemplate(CustomFieldTemplate cft, ICustomFieldEntity entity, Date valueDateFrom, Date valueDateTo, Integer valuePriority) {
        CustomFieldInstance cfi = CustomFieldInstance.fromTemplate(cft, entity);
        if (cft.isVersionable() && cft.getCalendar() == null) {
            cfi.setPeriodEndDate(valueDateTo);
            cfi.setPeriodStartDate(valueDateFrom);
            cfi.setPriority(valuePriority == null ? 0 : valuePriority);
        }
        return cfi;
    }

    /**
     * Check if values is empty when used in data entry/display for GUI (use XXXForGUI fields instead of serializedValue field )
     * 
     * @return True is value is empty
     */
    public boolean isValueEmptyForGui() {
        return getCfValue().isValueEmptyForGui();
    }

    /**
     * Check if values is empty when used in non-GUI data manipulation (use serializedValue instead of XXXForGUI fields)
     * 
     * @return True is value is empty
     */
    public boolean isValueEmpty() {
        return getCfValue().isValueEmpty();
    }

    /**
     * Check if date falls within period start and end dates
     * 
     * @param date Date to check
     * @return True/false
     */
    public boolean isCorrespondsToPeriod(Date date) {
        return (periodStartDate == null || date.compareTo(periodStartDate) >= 0) && (periodEndDate == null || date.before(periodEndDate));
    }

    /**
     * Check if dates match period start and end dates (strict match) or overlap period start and end dates (non-strict match)
     * 
     * @param startDate Period start date to check
     * @param endDate Period end date to check
     * @param strictMatch True If dates match period start and end dates (strict match) or False when overlap period start and end dates (non-strict match)
     * @return True if current period object corresponds to give dates and strict matching type
     */
    public boolean isCorrespondsToPeriod(Date startDate, Date endDate, boolean strictMatch) {

        if (strictMatch) {
            boolean match = (startDate == null && periodStartDate == null) || (startDate != null && periodStartDate != null && startDate.equals(periodStartDate));
            match = match && ((endDate == null && periodEndDate == null) || (endDate != null && periodEndDate != null && endDate.equals(periodEndDate)));
            return match;
        }
        // Check non-strict match case when dates overlap
        return DateUtils.isPeriodsOverlap(periodStartDate, periodEndDate, startDate, endDate);
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
    public void deserializeValue() {
        if (cfValue != null) {
            getCfValue().deserializeValue();
        }
    }

    @Override
    public String toString() {
        return String.format("CustomFieldInstance [code=%s, description=%s, appliesToEntity=%s, periodStartDate=%s, periodEndDate=%s, priority=%s, cfValue=%s, disabled=%s]", code,
            description,appliesToEntity, periodStartDate, periodEndDate, priority, cfValue, isDisabled());
    }

    public String toJson() {
        String result = code + ":";
        result += getCfValue().toJson(sdf);
        result+=",description:"+description;
        return result;
    }

    public Element toDomElement(Document doc) {
        Element customFieldTag = doc.createElement("customField");
        customFieldTag.setAttribute("code", code);
        customFieldTag.setAttribute("description", description);
        if (periodStartDate != null) {
            customFieldTag.setAttribute("periodStartDate", xmlsdf.format(periodStartDate));
        }
        if (periodEndDate != null) {
            customFieldTag.setAttribute("periodEndDate", xmlsdf.format(periodEndDate));
        }

        Text customFieldText = doc.createTextNode(getCfValue().toXmlText(xmlsdf));
        customFieldTag.appendChild(customFieldText);

        return customFieldTag;
    }
}