package org.meveo.model.crm;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.catalog.Calendar;

@Entity
@ExportIdentifier({ "code", "accountLevel", "provider" })
@Table(name = "CRM_CUSTOM_FIELD_TMPL", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "ACCOUNT_TYPE", "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CRM_CUSTOM_FLD_TMP_SEQ")
public class CustomFieldTemplate extends BusinessEntity {

    private static final long serialVersionUID = -1403961759495272885L;

    @Column(name = "FIELD_TYPE")
    @Enumerated(EnumType.STRING)
    private CustomFieldTypeEnum fieldType;

    @Column(name = "ACCOUNT_TYPE")
    @Enumerated(EnumType.STRING)
    private AccountLevelEnum accountLevel;

    @Column(name = "VALUE_REQUIRED")
    private boolean valueRequired;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "CRM_CUSTOM_FIELD_TMPL_VAL")
    private Map<String, String> listValues = new HashMap<String, String>();

    @Column(name = "VERSIONABLE")
    private boolean versionable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CALENDAR_ID")
    private Calendar calendar;

    @Column(name = "DEFAULT_VALUE", length = 50)
    private String defaultValue;
    
    @Column(name="ENTITY_CLAZZ")
    private String entityClazz;
    
    @Column(name="STORAGE_TYPE")
    @Enumerated(EnumType.STRING)
    private CustomFieldStorageTypeEnum storageType=CustomFieldStorageTypeEnum.SINGLE;

    @Transient
    private CustomFieldInstance instance;

    public CustomFieldTypeEnum getFieldType() {
        return fieldType;
    }

    public void setFieldType(CustomFieldTypeEnum fieldType) {
        this.fieldType = fieldType;
    }

    public AccountLevelEnum getAccountLevel() {
        return accountLevel;
    }

    public void setAccountLevel(AccountLevelEnum accountLevel) {
        this.accountLevel = accountLevel;
    }

    public boolean isValueRequired() {
        return valueRequired;
    }

    public void setValueRequired(boolean valueRequired) {
        this.valueRequired = valueRequired;
    }

    public Map<String, String> getListValues() {
        return listValues;
    }

    public void setListValues(Map<String, String> listValues) {
        this.listValues = listValues;
    }

    public void setVersionable(boolean versionable) {
        this.versionable = versionable;
        if(versionable)
        	this.storageType=CustomFieldStorageTypeEnum.LIST;
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

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

	public String getEntityClazz() {
		return entityClazz;
	}

	public void setEntityClazz(String entityClazz) {
		this.entityClazz = entityClazz;
	}

	public Object getDefaultValueConverted() {
        if (defaultValue != null) {
            if (fieldType == CustomFieldTypeEnum.DOUBLE) {
                return Double.parseDouble(defaultValue);
            } else if (fieldType == CustomFieldTypeEnum.LONG) {
                return Long.parseLong(defaultValue);
            } else if (fieldType == CustomFieldTypeEnum.DATE) {
                return null; // TODO implement deserialization from a date
            }
        }
        return defaultValue;
    }

    public void setInstance(CustomFieldInstance instance) {
        this.instance = instance;
    }

    public CustomFieldInstance getInstance() {
        return instance;
    }

	public CustomFieldStorageTypeEnum getStorageType() {
		return storageType;
	}

	public void setStorageType(CustomFieldStorageTypeEnum storageType) {
		this.storageType = storageType;
		if(storageType==CustomFieldStorageTypeEnum.LIST){
			valueRequired=true;
		}
	}
    
}