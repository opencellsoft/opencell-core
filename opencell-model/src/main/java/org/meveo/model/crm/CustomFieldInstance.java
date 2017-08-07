package org.meveo.model.crm;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.DatePeriod;
import org.meveo.model.EnableEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldValue;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

@Entity
@ExportIdentifier({ "appliesToEntity", "code", "period.from", "period.to" })
@Table(name = "crm_custom_field_inst", uniqueConstraints = @UniqueConstraint(columnNames = { "applies_to_uuid", "code", "period_start_date", "period_end_date" }))

@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {@Parameter(name = "sequence_name", value = "crm_custom_field_inst_seq"), })
@NamedQueries({
        @NamedQuery(name = "CustomFieldInstance.getCfiForCache", query = "select cfi from CustomFieldInstance cfi where cfi.disabled=false order by cfi.appliesToEntity"),
        @NamedQuery(name = "CustomFieldInstance.getCfiByCode", query = "select cfi from CustomFieldInstance cfi where cfi.appliesToEntity=:appliesToEntity and cfi.code=:code "),
        @NamedQuery(name = "CustomFieldInstance.getCfiByCodeAndDate", query = "select cfi from CustomFieldInstance cfi where cfi.appliesToEntity=:appliesToEntity and cfi.code=:code  and ((cfi.period.from<=:date and :date<cfi.period.to) or (cfi.period.from<=:date and cfi.period.to IS NULL) or (cfi.period.from IS NULL and :date<cfi.period.to)) order by cfi.priority desc "),
        @NamedQuery(name = "CustomFieldInstance.getCfiByCodeAndDateRange", query = "select cfi from CustomFieldInstance cfi where cfi.appliesToEntity=:appliesToEntity and cfi.code=:code  and (cfi.period.from=:dateFrom and cfi.period.to=:dateTo)  order by cfi.priority desc "),
        @NamedQuery(name = "CustomFieldInstance.getCfiByEntity", query = "select cfi from CustomFieldInstance cfi where cfi.appliesToEntity=:appliesToEntity "),
        @NamedQuery(name = "CustomFieldInstance.getCfiByEntityListForIndex", query = "select cfi from CustomFieldInstance cfi where cfi.appliesToEntity in :appliesToEntityList and cfi.code in (select cft.code from CustomFieldTemplate cft where cft.indexType is not null )"),
        @NamedQuery(name = "CustomFieldInstance.getCfiValueByCode", query = "select cfi.cfValue from CustomFieldInstance cfi where cfi.appliesToEntity=:appliesToEntity and cfi.code=:code "),
        @NamedQuery(name = "CustomFieldInstance.getCfiValueByCodeAndDate", query = "select cfi.cfValue from CustomFieldInstance cfi where cfi.appliesToEntity=:appliesToEntity and cfi.code=:code  and ((cfi.period.from<=:date and :date<cfi.period.to) or (cfi.period.from<=:date and cfi.period.to IS NULL) or (cfi.period.from IS NULL and :date<cfi.period.to)) order by cfi.priority desc ") })
public class CustomFieldInstance extends EnableEntity {

    private static final long serialVersionUID = 8691447585410651639L;

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static SimpleDateFormat xmlsdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    @Column(name = "code", nullable = false, length = 255)
    @Size(max = 255, min = 1)
    @NotNull
    private String code;

    @Column(name = "applies_to_uuid", nullable = false, length = 60)
    @Size(max = 60)
    @NotNull
    private String appliesToEntity;

    @AttributeOverrides({ @AttributeOverride(name = "from", column = @Column(name = "period_start_date")),
            @AttributeOverride(name = "to", column = @Column(name = "period_end_date")) })
    private DatePeriod period = new DatePeriod();

    @Column(name = "priority")
    private int priority;

    @Column(name = "description")
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

    public DatePeriod getPeriod() {
        if (period == null) {
            period = new DatePeriod();
        }
        return period;
    }
    
    public DatePeriod getPeriodRaw() {
        return period;
    }

    public void setPeriod(DatePeriod period) {
        this.period = period;
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
        
        if (getPeriodRaw() != null && !getPeriodRaw().isCorrespondsToPeriod(other.getPeriodRaw(),true)) {
            return false;
        } else if (getPeriodRaw() == null && (other.getPeriodRaw() != null && !other.getPeriodRaw().isEmpty())) {
            return false;
        }


        return true;//iod().isCorrespondsToPeriod(other.getPeriod(), true);
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
            cfi.setPeriod(new DatePeriod(cft.getCalendar().previousCalendarDate(valueDate), cft.getCalendar().nextCalendarDate(valueDate)));
        } else if (cft.isVersionable() && cft.getCalendar() == null) {
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
            cfi.setPeriod(new DatePeriod(valueDateFrom, valueDateTo));
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
        return String.format("CustomFieldInstance [code=%s, description=%s, appliesToEntity=%s, period=%s, priority=%s, cfValue=%s, disabled=%s]", code, description,
            appliesToEntity, period, priority, cfValue, isDisabled());
    }

    public String toJson() {
        String result = code + ":";
        result += getCfValue().toJson(sdf);
        result += ",description:" + description;
        return result;
    }

    public Element toDomElement(Document doc) {
        Element customFieldTag = doc.createElement("customField");
        customFieldTag.setAttribute("code", code);
        customFieldTag.setAttribute("description", description);
        if (period != null && period.getFrom() != null) {
            customFieldTag.setAttribute("periodStartDate", xmlsdf.format(period.getFrom()));
        }
        if (period != null && period.getTo() != null) {
            customFieldTag.setAttribute("periodEndDate", xmlsdf.format(period.getTo()));
        }

        Text customFieldText = doc.createTextNode(getCfValue().toXmlText(xmlsdf));
        customFieldTag.appendChild(customFieldText);

        return customFieldTag;
    }
}