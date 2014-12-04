package org.meveo.model.crm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.meveo.model.BusinessEntity;

@Entity
@Table(name = "CRM_CUSTOM_FIELD_TMPL")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CRM_CUSTOM_FLD_TMP_SEQ")
public class CustomFieldTemplate extends BusinessEntity {

	private static final long serialVersionUID = -1403961759495272885L;

	@Column(name = "FIELD_TYPE")
	@Enumerated
	private CustomFieldTypeEnum fieldType;

	@Column(name = "ACCOUNT_TYPE")
	@Enumerated
	private AccountLevelEnum accountLevel;

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

}
