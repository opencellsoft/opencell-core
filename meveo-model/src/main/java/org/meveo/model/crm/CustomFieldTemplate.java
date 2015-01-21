package org.meveo.model.crm;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.meveo.model.BusinessEntity;

@Entity
@Table(name = "CRM_CUSTOM_FIELD_TMPL", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CRM_CUSTOM_FLD_TMP_SEQ")
public class CustomFieldTemplate extends BusinessEntity {

	private static final long serialVersionUID = -1403961759495272885L;

	@Column(name = "FIELD_TYPE")
	@Enumerated
	private CustomFieldTypeEnum fieldType;

	@Column(name = "ACCOUNT_TYPE")
	@Enumerated
	private AccountLevelEnum accountLevel;

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

}
