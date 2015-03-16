package org.meveo.model.crm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.meveo.model.BusinessEntity;

@Entity
@Table(name = "CRM_CUSTOM_FIELD_TMPL", uniqueConstraints = @UniqueConstraint(columnNames = {"CODE","ACCOUNT_TYPE","PROVIDER_ID"}))
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

	@Transient
	private String stringValue;

	@Transient
	private Double doubleValue;

	@Transient
	private Long longValue;

	@Transient
	private Date dateValue;

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

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	public Double getDoubleValue() {
		return doubleValue;
	}

	public void setDoubleValue(Double doubleValue) {
		this.doubleValue = doubleValue;
	}

	public Long getLongValue() {
		return longValue;
	}

	public void setLongValue(Long longValue) {
		this.longValue = longValue;
	}

	public Date getDateValue() {
		return dateValue;
	}

    public void setDateValue(Date dateValue) {
        this.dateValue = dateValue;
    }
    
    /**
     * Check if value is set
     * 
     * @return True if no value is set
     */
    public boolean isValueEmpty() {
        return (stringValue == null && (fieldType == CustomFieldTypeEnum.STRING || fieldType == CustomFieldTypeEnum.LIST))
                || (dateValue == null && fieldType == CustomFieldTypeEnum.DATE) || (longValue == null && fieldType == CustomFieldTypeEnum.LONG)
                || (doubleValue == null && fieldType == CustomFieldTypeEnum.DOUBLE);
    }
}